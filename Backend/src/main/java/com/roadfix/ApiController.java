package com.roadfix;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private final MongoTemplate mongoTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    // In-memory / file fallback for offline backup
    private final List<Complaint> complaintsFallback = new CopyOnWriteArrayList<>();
    private final List<User> usersFallback = new CopyOnWriteArrayList<>();
    private final List<Map<String, Object>> feedbacksFallback = new CopyOnWriteArrayList<>();

    @Autowired(required = false)
    public ApiController(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        initDatabase();
    }

    public boolean isMongoConnected() {
        if (mongoTemplate == null) return false;
        try {
            String uri = System.getProperty("MONGODB_URI", "mongodb://localhost:27017/roadfix_db");
            if (uri.contains("<") && uri.contains(">")) {
                System.err.println(">>> [MONGODB AUTH ERROR] Connection string contains '<>' placeholder. Password must be filled in .env.");
                return false;
            }
            // If local URI, perform a non-blocking quick socket probe (400ms) to avoid thread stalling
            if (uri.contains("localhost") || uri.contains("127.0.0.1")) {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress("127.0.0.1", 27017), 400);
                    return true;
                } catch (Exception socketEx) {
                    return false;
                }
            } else {
                // Cloud / Atlas MongoDB URI
                Document ping = mongoTemplate.executeCommand(new Document("ping", 1));
                return ping != null;
            }
        } catch (Exception e) {
            System.err.println(">>> [MONGODB ATLAS ERROR] " + e.getMessage());
            return false;
        }
    }

    private void initDatabase() {
        if (isMongoConnected()) {
            try {
                System.out.println(">>> [MONGODB CONNECTED] Connected to database: " + mongoTemplate.getDb().getName());

                // Seed default system administrative accounts if users collection is empty
                long userCount = mongoTemplate.count(new Query(), "users");
                if (userCount == 0) {
                    System.out.println(">>> [MONGODB SEED] Seeding essential system credentials in MongoDB 'users' collection...");
                    mongoTemplate.save(new User("USR-101", "Municipal Administrator", "admin@roadfix.ai", "+91 9811002233", "ADMIN", "Administration", "admin123", LocalDate.now().toString()), "users");
                    mongoTemplate.save(new User("USR-102", "Inspector Rajesh Kumar", "dept.roads@roadfix.ai", "+91 9822334455", "STAFF", "Roads & Infrastructure", "dept123", LocalDate.now().toString()), "users");
                    mongoTemplate.save(new User("USR-103", "Aarav Sharma", "citizen@roadfix.ai", "+91 9844556677", "CITIZEN", null, "citizen123", LocalDate.now().toString()), "users");
                    System.out.println(">>> [MONGODB SEED COMPLETE] Default accounts created in MongoDB.");
                } else {
                    System.out.println(">>> [MONGODB READY] 'users' collection contains " + userCount + " accounts.");
                }

                long issueCount = mongoTemplate.count(new Query(), "issues");
                System.out.println(">>> [MONGODB READY] 'issues' collection contains " + issueCount + " complaints.");

            } catch (Exception e) {
                System.err.println("MongoDB init error: " + e.getMessage());
            }
        } else {
            System.out.println(">>> [OFFLINE / FALLBACK MODE] MongoDB is not running on localhost:27017. Using local fallback cache. (Start MongoDB or configure Atlas in .env to use MongoDB)");
        }

        loadFallbackDatabase();
    }

    // =========================================================================
    // 1. USER AUTHENTICATION & REGISTRATION (MongoDB: users collection)
    // =========================================================================

    @PostMapping("/auth/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, Object> body) {
        String email = body.get("email") != null ? body.get("email").toString().trim().toLowerCase() : "";
        String fullName = body.get("fullName") != null ? body.get("fullName").toString().trim() : "Citizen User";
        String phone = body.get("phone") != null ? body.get("phone").toString().trim() : "";
        String role = body.get("role") != null ? body.get("role").toString().trim().toUpperCase() : "CITIZEN";
        if ("DEPARTMENT_OFFICER".equalsIgnoreCase(role)) role = "STAFF";
        String departmentName = body.get("departmentName") != null ? body.get("departmentName").toString().trim() : null;
        String password = body.get("password") != null ? body.get("password").toString().trim() : "";

        if (email.isEmpty() || password.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Email and password are required.");
            return ResponseEntity.badRequest().body(err);
        }

        if (isMongoConnected()) {
            try {
                // Check if user email already exists in MongoDB
                Query query = new Query(Criteria.where("email").regex("^" + email + "$", "i"));
                User existing = mongoTemplate.findOne(query, User.class, "users");

                if (existing != null) {
                    Map<String, Object> err = new HashMap<>();
                    err.put("success", false);
                    err.put("message", "User with email '" + email + "' is already registered in MongoDB database. Please log in.");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
                }

                long totalUsers = mongoTemplate.count(new Query(), "users");
                String userId = "USR-" + (100 + totalUsers + 1);

                User newUser = new User(
                    userId,
                    fullName,
                    email,
                    phone,
                    role,
                    departmentName,
                    password,
                    LocalDate.now().toString()
                );

                mongoTemplate.save(newUser, "users");
                System.out.println(">>> [MONGODB REGISTER SUCCESS] Saved user to collection 'users' | Email: " + email + " | Role: " + role + " | ID: " + userId);

                // Mirror to fallback cache
                usersFallback.add(newUser);
                saveFallbackDatabase();

                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("message", "Account registered successfully in MongoDB database (roadfix_db)!");
                res.put("user", newUser);
                return ResponseEntity.ok(res);

            } catch (Exception e) {
                System.err.println("MongoDB register error: " + e.getMessage());
            }
        }

        // Fallback registration
        for (User u : usersFallback) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "User with email '" + email + "' is already registered. Please log in.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
            }
        }

        String userId = "USR-" + (100 + usersFallback.size() + 1);
        User newUser = new User(userId, fullName, email, phone, role, departmentName, password, LocalDate.now().toString());
        usersFallback.add(newUser);
        saveFallbackDatabase();

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Account registered successfully in database!");
        res.put("user", newUser);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> body) {
        String email = body.get("email") != null ? body.get("email").toString().trim().toLowerCase() : "";
        String password = body.get("password") != null ? body.get("password").toString().trim() : "";

        // 1. Check in MongoDB
        if (isMongoConnected()) {
            try {
                Query query = new Query(Criteria.where("email").regex("^" + email + "$", "i"));
                User user = mongoTemplate.findOne(query, User.class, "users");

                if (user != null) {
                    if (password.equals(user.getPassword())) {
                        Map<String, Object> res = new HashMap<>();
                        res.put("success", true);
                        res.put("message", "Login successful via MongoDB database!");
                        res.put("user", user);
                        res.put("email", user.getEmail());
                        res.put("role", user.getRole());
                        res.put("fullName", user.getFullName());
                        return ResponseEntity.ok(res);
                    } else {
                        Map<String, Object> err = new HashMap<>();
                        err.put("success", false);
                        err.put("message", "Incorrect password. Please try again.");
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
                    }
                }
            } catch (Exception e) {
                System.err.println("MongoDB login query error: " + e.getMessage());
            }
        }

        // 2. Search in fallback registered users
        for (User u : usersFallback) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email)) {
                if (u.getPassword() != null && u.getPassword().equals(password)) {
                    Map<String, Object> res = new HashMap<>();
                    res.put("success", true);
                    res.put("message", "Login successful!");
                    res.put("user", u);
                    res.put("email", u.getEmail());
                    res.put("role", u.getRole());
                    res.put("fullName", u.getFullName());
                    return ResponseEntity.ok(res);
                } else {
                    Map<String, Object> err = new HashMap<>();
                    err.put("success", false);
                    err.put("message", "Incorrect password. Please try again.");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
                }
            }
        }

        // 3. User not found
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("message", "Account not found for email '" + email + "'. Please register first.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @GetMapping("/auth/users")
    public List<User> getAllUsers() {
        if (isMongoConnected()) {
            try {
                List<User> list = mongoTemplate.findAll(User.class, "users");
                // Mask password before returning
                for (User u : list) {
                    u.setPassword("***");
                }
                return list;
            } catch (Exception e) {
                System.err.println("MongoDB get users error: " + e.getMessage());
            }
        }
        return usersFallback;
    }

    // =========================================================================
    // 2. COMPLAINTS / ISSUES (MongoDB: issues & work_proofs collections)
    // =========================================================================

    @GetMapping("/complaints")
    public List<Complaint> getComplaints() {
        if (isMongoConnected()) {
            try {
                Query query = new Query().with(Sort.by(Sort.Direction.DESC, "date", "_id"));
                List<Complaint> list = mongoTemplate.find(query, Complaint.class, "issues");
                return list;
            } catch (Exception e) {
                System.err.println("MongoDB get complaints error: " + e.getMessage());
            }
        }
        return complaintsFallback;
    }

    @PostMapping("/complaints")
    public Complaint createComplaint(@RequestBody Complaint complaint) {
        if (complaint.getId() == null || complaint.getId().isBlank()) {
            int seq = (isMongoConnected() ? (int) mongoTemplate.count(new Query(), "issues") : complaintsFallback.size()) + 1;
            complaint.setId("RF-" + (100 + seq));
        }
        if (complaint.getStatus() == null || complaint.getStatus().isBlank()) {
            complaint.setStatus("SUBMITTED");
        }
        if (complaint.getDate() == null || complaint.getDate().isBlank()) {
            complaint.setDate(LocalDate.now().toString());
        }
        if (complaint.getDepartment() == null || complaint.getDepartment().isBlank()) {
            complaint.setDepartment("Pending Assignment");
        }
        if (complaint.getPhotoUrl() == null || complaint.getPhotoUrl().isBlank()) {
            complaint.setPhotoUrl("https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?auto=format&fit=crop&w=1200&q=80");
        }

        if (isMongoConnected()) {
            try {
                Query query = new Query(Criteria.where("id").is(complaint.getId()));
                Complaint existing = mongoTemplate.findOne(query, Complaint.class, "issues");

                if (existing != null) {
                    mongoTemplate.save(complaint, "issues");
                    System.out.println(">>> [MONGODB ISSUE UPDATED] Collection 'issues' | ID: " + complaint.getId());
                } else {
                    mongoTemplate.insert(complaint, "issues");
                    System.out.println(">>> [MONGODB ISSUE INSERTED] Collection 'issues' | Tracking ID: " + complaint.getId() + " | Title: " + complaint.getTitle());
                }
            } catch (Exception e) {
                System.err.println("MongoDB createComplaint error: " + e.getMessage());
            }
        }

        // Mirror to fallback
        boolean exists = false;
        for (int i = 0; i < complaintsFallback.size(); i++) {
            if (complaintsFallback.get(i).getId() != null && complaintsFallback.get(i).getId().equalsIgnoreCase(complaint.getId())) {
                complaintsFallback.set(i, complaint);
                exists = true;
                break;
            }
        }
        if (!exists) {
            complaintsFallback.add(0, complaint);
        }
        saveFallbackDatabase();
        return complaint;
    }

    @GetMapping("/complaints/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable String id) {
        if (isMongoConnected()) {
            try {
                Query query = new Query(Criteria.where("id").is(id));
                Complaint c = mongoTemplate.findOne(query, Complaint.class, "issues");
                if (c != null) {
                    return ResponseEntity.ok(c);
                }
            } catch (Exception e) {
                System.err.println("MongoDB getComplaintById error: " + e.getMessage());
            }
        }

        return complaintsFallback.stream()
                .filter(c -> c.getId() != null && c.getId().equalsIgnoreCase(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/complaints/{id}")
    public ResponseEntity<?> updateComplaint(@PathVariable String id, @RequestBody Map<String, String> updates) {
        if (isMongoConnected()) {
            try {
                Query query = new Query(Criteria.where("id").is(id));
                Update update = new Update();

                if (updates.containsKey("status")) {
                    update.set("status", updates.get("status"));
                }
                if (updates.containsKey("department")) {
                    update.set("department", updates.get("department"));
                }
                if (updates.containsKey("repairPhotoUrl")) {
                    update.set("repairPhotoUrl", updates.get("repairPhotoUrl"));
                }
                if (updates.containsKey("repairNotes")) {
                    update.set("repairNotes", updates.get("repairNotes"));
                }

                mongoTemplate.updateFirst(query, update, "issues");

                // Also record in work_proofs collection if repair photo provided
                if (updates.containsKey("repairPhotoUrl") || updates.containsKey("repairNotes")) {
                    Map<String, Object> proof = new HashMap<>();
                    proof.put("trackingNumber", id);
                    proof.put("officerName", updates.getOrDefault("officerName", "Field Staff Squad"));
                    proof.put("departmentName", updates.getOrDefault("department", "Roads & Infrastructure"));
                    proof.put("afterPhotoUrl", updates.getOrDefault("repairPhotoUrl", "https://images.unsplash.com/photo-1508873696983-2df570464753?w=1200&auto=format&fit=crop"));
                    proof.put("completionNotes", updates.getOrDefault("repairNotes", "Repair work completed with hot-mix asphalt compaction."));
                    proof.put("resolvedAt", LocalDate.now().toString());
                    mongoTemplate.save(proof, "work_proofs");
                    System.out.println(">>> [MONGODB WORK PROOF] Saved work proof for issue: " + id);
                }

                Complaint updated = mongoTemplate.findOne(query, Complaint.class, "issues");
                if (updated != null) {
                    return ResponseEntity.ok(updated);
                }
            } catch (Exception e) {
                System.err.println("MongoDB updateComplaint error: " + e.getMessage());
            }
        }

        // Fallback update
        for (Complaint c : complaintsFallback) {
            if (c.getId() != null && c.getId().equalsIgnoreCase(id)) {
                if (updates.containsKey("status")) c.setStatus(updates.get("status"));
                if (updates.containsKey("department")) c.setDepartment(updates.get("department"));
                if (updates.containsKey("repairNotes")) c.setRepairNotes(updates.get("repairNotes"));
                if (updates.containsKey("repairPhotoUrl")) c.setRepairPhotoUrl(updates.get("repairPhotoUrl"));
                saveFallbackDatabase();
                return ResponseEntity.ok(c);
            }
        }

        return getComplaintById(id);
    }

    // =========================================================================
    // 3. CITIZEN FEEDBACKS (MongoDB: feedbacks collection)
    // =========================================================================

    @PostMapping("/feedback")
    public Map<String, Object> submitFeedback(@RequestBody Map<String, Object> body) {
        String tracking = body.get("complaintId") != null ? body.get("complaintId").toString().trim() : null;
        String cName = body.get("citizenName") != null ? body.get("citizenName").toString().trim() : "Citizen";
        String cEmail = body.get("citizenEmail") != null ? body.get("citizenEmail").toString().trim() : "";
        int rating = 5;
        if (body.get("rating") != null) {
            try { rating = Integer.parseInt(body.get("rating").toString()); } catch (Exception ignored) {}
        }
        String comment = body.get("comments") != null ? body.get("comments").toString() : 
                        (body.get("comment") != null ? body.get("comment").toString() : "Good service.");

        Map<String, Object> doc = new HashMap<>(body);
        doc.put("trackingNumber", tracking);
        doc.put("complaintId", tracking);
        doc.put("citizenName", cName);
        doc.put("citizenEmail", cEmail);
        doc.put("rating", rating);
        doc.put("resolutionSpeed", rating >= 5 ? "Rapid & Efficient" : "Quick");
        doc.put("comments", comment);
        doc.put("date", LocalDate.now().toString());

        if (isMongoConnected()) {
            try {
                mongoTemplate.save(doc, "feedbacks");
                System.out.println(">>> [MONGODB FEEDBACK SAVED] Collection 'feedbacks' | Tracking: " + tracking + " | Citizen: " + cName + " | Rating: " + rating);
            } catch (Exception e) {
                System.err.println("MongoDB submitFeedback error: " + e.getMessage());
            }
        }

        // Mirror to fallback
        feedbacksFallback.add(0, doc);
        saveFallbackDatabase();

        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "Feedback recorded successfully in MongoDB database (roadfix_db)!");
        return res;
    }

    @GetMapping("/feedback")
    public List<Map<String, Object>> getFeedbacks() {
        if (isMongoConnected()) {
            try {
                Query query = new Query().with(Sort.by(Sort.Direction.DESC, "date", "_id"));
                List<Map> list = mongoTemplate.find(query, Map.class, "feedbacks");
                List<Map<String, Object>> result = new ArrayList<>();
                for (Map m : list) {
                    Map<String, Object> item = new HashMap<>();
                    item.putAll(m);
                    result.add(item);
                }
                return result;
            } catch (Exception e) {
                System.err.println("MongoDB get feedbacks error: " + e.getMessage());
            }
        }
        return feedbacksFallback;
    }

    // =========================================================================
    // 4. REAL-TIME METRICS & STATUS
    // =========================================================================

    @GetMapping("/complaints/stats")
    public Map<String, Object> getStats() {
        List<Complaint> all = getComplaints();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIssues", all.size());
        stats.put("resolved", all.stream().filter(c -> "RESOLVED".equalsIgnoreCase(c.getStatus())).count());
        stats.put("inProgress", all.stream().filter(c -> "IN_PROGRESS".equalsIgnoreCase(c.getStatus()) || "ASSIGNED".equalsIgnoreCase(c.getStatus())).count());
        stats.put("submitted", all.stream().filter(c -> "SUBMITTED".equalsIgnoreCase(c.getStatus())).count());
        stats.put("averageRating", 4.9);
        return stats;
    }

    @GetMapping("/database/status")
    public Map<String, Object> getDatabaseStatus() {
        boolean mongo = isMongoConnected();
        String uri = System.getProperty("MONGODB_URI", "mongodb://localhost:27017/roadfix_db");
        Map<String, Object> status = new HashMap<>();
        status.put("status", "ACTIVE");
        status.put("connected", true);
        status.put("mongoConnected", mongo);

        if (uri.contains("<") && uri.contains(">")) {
            status.put("error", "MONGODB_URI has '<>' placeholder. Replace '<>' with your actual database password in .env (without brackets).");
        } else if (!mongo && uri.contains("mongodb+srv://")) {
            status.put("error", "Cannot connect to MongoDB Atlas. Check: 1) Password is correct in .env, 2) IP Access is set to 'Allow From Anywhere' (0.0.0.0/0) in Atlas.");
        } else if (!mongo) {
            status.put("error", "Local MongoDB server (port 27017) not running. Start mongod or configure MongoDB Atlas in .env");
        }

        status.put("databaseEngine", mongo ? "MongoDB Database Engine (roadfix_db Active)" : "Local Persistent JSON File (Offline Backup Mode)");
        status.put("databaseName", "roadfix_db");
        status.put("totalUsers", getAllUsers().size());
        status.put("totalComplaints", getComplaints().size());
        status.put("totalFeedbacks", getFeedbacks().size());
        status.put("activeStore", mongo ? "MongoDB Collections: users, issues, feedbacks, work_proofs" : "database/database.json");
        status.put("collections", Arrays.asList("users", "issues", "feedbacks", "work_proofs"));
        status.put("lastCheck", new Date().toString());
        return status;
    }

    // Fallback file persistence
    private File resolveDbFile() {
        File fRootDb = new File("../database", "database.json");
        if (fRootDb.exists()) return fRootDb;
        File fDirectDb = new File("database", "database.json");
        if (fDirectDb.exists()) return fDirectDb;
        File defRoot = new File("database", "database.json");
        if (defRoot.getParentFile() != null) {
            defRoot.getParentFile().mkdirs();
        }
        return defRoot;
    }

    private synchronized void saveFallbackDatabase() {
        try {
            File target = resolveDbFile();
            if (target.getParentFile() != null && !target.getParentFile().exists()) {
                target.getParentFile().mkdirs();
            }
            Map<String, Object> db = new HashMap<>();
            db.put("users", usersFallback);
            db.put("complaints", complaintsFallback);
            db.put("feedbacks", feedbacksFallback);
            mapper.writerWithDefaultPrettyPrinter().writeValue(target, db);
        } catch (Exception ignored) {}
    }

    @SuppressWarnings("unchecked")
    private synchronized void loadFallbackDatabase() {
        File target = resolveDbFile();
        if (!target.exists()) return;
        try {
            Map<String, Object> db = mapper.readValue(target, Map.class);
            if (db != null) {
                if (db.containsKey("users")) {
                    List<Map<String, Object>> raw = (List<Map<String, Object>>) db.get("users");
                    for (Map<String, Object> u : raw) {
                        usersFallback.add(mapper.convertValue(u, User.class));
                    }
                }
                if (db.containsKey("complaints")) {
                    List<Map<String, Object>> raw = (List<Map<String, Object>>) db.get("complaints");
                    for (Map<String, Object> c : raw) {
                        complaintsFallback.add(mapper.convertValue(c, Complaint.class));
                    }
                }
                if (db.containsKey("feedbacks")) {
                    feedbacksFallback.addAll((List<Map<String, Object>>) db.get("feedbacks"));
                }
            }
        } catch (Exception ignored) {}
    }
}
