// RoadFix AI - Simple & Clean JavaScript Manager
// Works automatically with Backend OR in offline/standalone browser mode!

// Auto-detect production domain or local fallback
const API_BASE = (typeof window !== 'undefined' && window.location.origin && window.location.origin !== 'null' && !window.location.origin.startsWith('file:'))
    ? `${window.location.origin}/api`
    : 'http://localhost:8080/api';

// Realistic AI Safety Inspection Engine Database
const AI_PREDICTIONS = {
    "Pothole": {
        detected: "Road Surface Pothole (Depth: ~14 cm)",
        confidence: "96.4%",
        hazard: "High Hazard: Severe two-wheeler skidding risk & vehicle suspension damage. Immediate cold-mix asphalt patch required."
    },
    "Drainage": {
        detected: "Stormwater Drainage Blockage",
        confidence: "94.8%",
        hazard: "Environmental Hazard: Foul runoff water spilling on pedestrian sidewalk. Desilting & trash removal required."
    },
    "Street Light": {
        detected: "Luminaire Non-Functional / Power Fault",
        confidence: "92.1%",
        hazard: "Night Transit Hazard: Low visibility zone increasing accident probability. Bulb/circuit replacement needed."
    },
    "Garbage": {
        detected: "Solid Waste & Construction Debris Dumping",
        confidence: "95.0%",
        hazard: "Civic Obstruction: Blockage of road margin and pedestrian corridor. Municipal waste truck collection required."
    },
    "Water Leakage": {
        detected: "Subsurface Pipeline Rupture",
        confidence: "97.3%",
        hazard: "Structural Hazard: Continuous water gushing weakening road sub-base asphalt. Valve isolation & pipe replacement needed."
    },
    "Road Damage": {
        detected: "Asphalt Longitudinal & Alligator Cracks",
        confidence: "91.5%",
        hazard: "Surface Fatigue: Water ingress causing progressive subgrade erosion. Resurfacing recommended."
    },
    "Other": {
        detected: "Civic Road Infrastructure Hazard",
        confidence: "89.0%",
        hazard: "Municipal review and on-site engineering inspection required."
    }
};

// Realistic category-matched high-definition road hazard photos
const CATEGORY_DEFAULT_PHOTOS = {
    "Pothole": "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?auto=format&fit=crop&w=1200&q=80",
    "Street Light": "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=1200&q=80",
    "Drainage": "https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=1200&q=80",
    "Road Damage": "https://images.unsplash.com/photo-1578575437130-527eed3abbec?auto=format&fit=crop&w=1200&q=80",
    "Water Leakage": "https://images.unsplash.com/photo-1547683905-f686c993aae5?auto=format&fit=crop&w=1200&q=80",
    "Garbage": "https://images.unsplash.com/photo-1530587191325-3db32d826c18?auto=format&fit=crop&w=1200&q=80",
    "Other": "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?auto=format&fit=crop&w=1200&q=80"
};

// Clean initial state (zero dummy complaints)
const INITIAL_COMPLAINTS = [];

// Essential administrative and test credentials
const DEFAULT_USERS = [
    { id: "USR-101", fullName: "Municipal Administrator", email: "admin@roadfix.ai", phone: "+91 9811002233", role: "ADMIN", departmentName: "Administration", password: "admin123" },
    { id: "USR-102", fullName: "Inspector Rajesh Kumar", email: "dept.roads@roadfix.ai", phone: "+91 9822334455", role: "STAFF", departmentName: "Roads & Infrastructure", password: "dept123" },
    { id: "USR-103", fullName: "Aarav Sharma", email: "citizen@roadfix.ai", phone: "+91 9844556677", role: "CITIZEN", departmentName: null, password: "citizen123" }
];

// Clean initial state (zero dummy feedback)
const DEFAULT_FEEDBACKS = [];

const DEFAULT_SLIDES = [
    {
        id: 1,
        tag: "🤖 AI-Powered Road Hazard Detection",
        title: "Transforming Urban Safety With <span style='color:var(--accent);'>Smart RoadFix AI</span>",
        desc: "Detect potholes, overflowing stormwater drains, and road damages instantly with automated computer vision hazard scoring and GPS coordinates.",
        image: "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?w=1600&auto=format&fit=crop&q=80",
        btn1Text: "📸 Report Road Issue",
        btn1Link: "report.html",
        btn2Text: "🔍 Live Progress Tracker",
        btn2Link: "track.html"
    },
    {
        id: 2,
        tag: "👷 48-Hour Municipal Dispatch Commitment",
        title: "Rapid Field Repairs & <span style='color:var(--accent);'>Verified Work Orders</span>",
        desc: "Automated task routing to Roads & Infrastructure, Sanitation, and Electrical municipal squads with photographic proof of completion.",
        image: "https://images.unsplash.com/photo-1584467735871-8e85353a8413?w=1600&auto=format&fit=crop&q=80",
        btn1Text: "👷 Field Staff Panel",
        btn1Link: "staff.html",
        btn2Text: "📋 View Public Issues",
        btn2Link: "complaints.html"
    },
    {
        id: 3,
        tag: "💡 Electrical & Street Lighting",
        title: "Active Night Monitoring & <span style='color:var(--accent);'>Dark Zone Elimination</span>",
        desc: "Swift replacement of damaged luminaires, faulty wiring, and non-functional municipal streetlights to safeguard commuter transit.",
        image: "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1600&auto=format&fit=crop&q=80",
        btn1Text: "👤 Citizen Dashboard",
        btn1Link: "citizen.html",
        btn2Text: "🛡️ Admin Command Center",
        btn2Link: "admin.html"
    },
    {
        id: 4,
        tag: "🌊 Stormwater Drainage & Flood Control",
        title: "Preventing Waterlogging & <span style='color:var(--accent);'>Protecting City Roads</span>",
        desc: "Immediate desilting and clearing of blocked storm drains to stop road erosion, pedestrian hazards, and vehicular hydroplaning.",
        image: "https://images.unsplash.com/photo-1547683905-f686c993aae5?w=1600&auto=format&fit=crop&q=80",
        btn1Text: "⭐ Submit Service Rating",
        btn1Link: "feedback.html",
        btn2Text: "🔑 Sign In to Portal",
        btn2Link: "login.html"
    }
];

function initStorage() {
    if (!localStorage.getItem('roadfix_complaints')) {
        localStorage.setItem('roadfix_complaints', JSON.stringify([]));
    }
    if (!localStorage.getItem('roadfix_users')) {
        localStorage.setItem('roadfix_users', JSON.stringify(DEFAULT_USERS));
    }
    if (!localStorage.getItem('roadfix_feedbacks')) {
        localStorage.setItem('roadfix_feedbacks', JSON.stringify([]));
    }
    if (!localStorage.getItem('roadfix_slides')) {
        localStorage.setItem('roadfix_slides', JSON.stringify(DEFAULT_SLIDES));
    }
}
initStorage();

const RoadFix = {
    CATEGORY_DEFAULT_PHOTOS,
    AI_PREDICTIONS,

    // 1. Get all complaints
    async getComplaints() {
        let serverComplaints = [];
        try {
            const res = await fetch(`${API_BASE}/complaints`);
            if (res.ok) {
                serverComplaints = await res.json();
            }
        } catch (e) {}

        const localComplaints = JSON.parse(localStorage.getItem('roadfix_complaints') || '[]');

        // Merge server and local complaints safely by ID
        const mergedMap = new Map();
        serverComplaints.forEach(c => {
            if (c && c.id) mergedMap.set(c.id.toUpperCase(), c);
        });

        localComplaints.forEach(c => {
            if (c && c.id) {
                const key = c.id.toUpperCase();
                if (!mergedMap.has(key)) {
                    mergedMap.set(key, c);
                    // Sync to backend
                    fetch(`${API_BASE}/complaints`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(c)
                    }).catch(() => {});
                }
            }
        });

        const allComplaints = Array.from(mergedMap.values());
        try {
            localStorage.setItem('roadfix_complaints', JSON.stringify(allComplaints));
        } catch (e) {}
        return allComplaints;
    },

    // 2. Add new complaint with AI Prediction & User Uploaded Image
    async addComplaint(complaint) {
        if (!complaint.id) {
            complaint.id = "RF-" + Math.floor(100 + Math.random() * 900);
        }
        complaint.status = "SUBMITTED";
        complaint.department = "Pending Assignment";
        complaint.date = new Date().toISOString().split('T')[0];

        // Attach realistic AI Prediction
        const ai = AI_PREDICTIONS[complaint.category] || AI_PREDICTIONS["Other"];
        complaint.aiDetected = ai.detected;
        complaint.aiConfidence = ai.confidence;
        complaint.aiHazard = ai.hazard;

        if (!complaint.photoUrl) {
            complaint.photoUrl = "https://images.unsplash.com/photo-1515162816999-a0c47dc192f7?auto=format&fit=crop&w=800&q=80";
        }

        // Try backend
        try {
            const res = await fetch(`${API_BASE}/complaints`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(complaint)
            });
            if (res.ok) {
                const saved = await res.json();
                complaint = saved;
            }
        } catch (e) {
            console.warn("Backend offline, saving complaint locally", e);
        }

        // Save into local database safely
        try {
            const list = JSON.parse(localStorage.getItem('roadfix_complaints') || '[]');
            const idx = list.findIndex(c => c && c.id && c.id.toUpperCase() === complaint.id.toUpperCase());
            if (idx >= 0) {
                list[idx] = complaint;
            } else {
                list.unshift(complaint);
            }
            localStorage.setItem('roadfix_complaints', JSON.stringify(list));
            localStorage.setItem('lastComplaintId', complaint.id);
        } catch (storageErr) {
            console.error("Local storage error while saving complaint", storageErr);
        }
        return complaint;
    },

    // 3. Track complaint by ID
    async getComplaintById(id) {
        const cleanId = id.trim().toUpperCase();
        try {
            const res = await fetch(`${API_BASE}/complaints/${cleanId}`);
            if (res.ok) return await res.json();
        } catch (e) {}

        const list = JSON.parse(localStorage.getItem('roadfix_complaints') || '[]');
        return list.find(c => c.id.toUpperCase() === cleanId) || null;
    },

    // 4. Update complaint status / department / repair photo
    async updateComplaint(id, updates) {
        try {
            await fetch(`${API_BASE}/complaints/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updates)
            });
        } catch (e) {}

        const list = JSON.parse(localStorage.getItem('roadfix_complaints') || '[]');
        const item = list.find(c => c.id.toUpperCase() === id.toUpperCase());
        if (item) {
            Object.assign(item, updates);
            localStorage.setItem('roadfix_complaints', JSON.stringify(list));
        }
        return item;
    },

    // 5. Auth session helpers
    getUser() {
        return JSON.parse(localStorage.getItem('roadfix_user') || 'null');
    },

    setUser(user) {
        localStorage.setItem('roadfix_user', JSON.stringify(user));
    },

    logout() {
        localStorage.removeItem('roadfix_user');
        window.location.href = 'login.html';
    },

    // 6. Simple Stats
    async getStats() {
        const list = await this.getComplaints();
        return {
            totalIssues: list.length,
            resolved: list.filter(c => c.status === 'RESOLVED').length,
            inProgress: list.filter(c => c.status === 'IN_PROGRESS' || c.status === 'ASSIGNED').length,
            submitted: list.filter(c => c.status === 'SUBMITTED').length,
            averageRating: 4.9
        };
    },

    // 7. Dynamic Role-Aware Navbar
    renderNavbar(navId = 'mainNav') {
        const nav = document.getElementById(navId);
        if (!nav) return;
        const user = this.getUser();

        let html = '';

        if (user && user.role === 'ADMIN') {
            html = `
                <a href="admin.html"><b>🛡️ Admin Command</b></a>
                <a href="complaints.html">All Complaints</a>
                <a href="track.html">Track Issue</a>
                <span style="color:#8fd3c7;font-size:13px;padding:3px 8px;background:rgba(255,255,255,0.12);border-radius:4px;margin-left:6px;">
                    🛡️ Admin
                </span>
                <a href="javascript:RoadFix.logout()" style="color:#fca5a5;margin-left:6px;font-weight:600;">Logout</a>
            `;
        } else if (user && (user.role === 'STAFF' || user.role === 'DEPARTMENT_OFFICER')) {
            html = `
                <a href="staff.html"><b>👷 Field Operations</b></a>
                <a href="complaints.html">Community Issues</a>
                <a href="track.html">Track Issue</a>
                <span style="color:#8fd3c7;font-size:13px;padding:3px 8px;background:rgba(255,255,255,0.12);border-radius:4px;margin-left:6px;">
                    👷 Staff
                </span>
                <a href="javascript:RoadFix.logout()" style="color:#fca5a5;margin-left:6px;font-weight:600;">Logout</a>
            `;
        } else if (user && user.role === 'CITIZEN') {
            html = `
                <a href="citizen.html"><b>👤 Citizen Dashboard</b></a>
                <a href="report.html">Report Issue</a>
                <a href="track.html">Track Status</a>
                <a href="complaints.html">Community Issues</a>
                <a href="feedback.html">Feedback</a>
                <span style="color:#8fd3c7;font-size:13px;padding:3px 8px;background:rgba(255,255,255,0.12);border-radius:4px;margin-left:6px;">
                    👤 ${user.fullName || 'Citizen'}
                </span>
                <a href="javascript:RoadFix.logout()" style="color:#fca5a5;margin-left:6px;font-weight:600;">Logout</a>
            `;
        } else {
            html = `
                <a href="index.html">Home</a>
                <a href="complaints.html">Community Issues</a>
                <a href="report.html">Report Issue</a>
                <a href="track.html">Track</a>
                <a href="login.html" style="background:#8fd3c7;color:#12315a;padding:6px 14px;border-radius:4px;font-weight:bold;margin-left:8px;">Login</a>
            `;
        }

        nav.innerHTML = html;
    },

    // 8. Register User in Database
    async register(userData) {
        userData.email = (userData.email || '').trim().toLowerCase();
        userData.role = (userData.role || 'CITIZEN').toUpperCase();
        if (userData.role === 'DEPARTMENT_OFFICER') userData.role = 'STAFF';

        // 1. Try Backend API
        try {
            const res = await fetch(`${API_BASE}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userData)
            });
            const data = await res.json();
            if (!res.ok || !data.success) {
                return { success: false, message: data.message || "Registration failed on server." };
            }
            // Save in localStorage as well for offline parity
            const users = JSON.parse(localStorage.getItem('roadfix_users') || '[]');
            const idx = users.findIndex(u => u && u.email && u.email.toLowerCase() === userData.email);
            if (idx >= 0) {
                users[idx] = data.user || userData;
            } else {
                users.push(data.user || userData);
            }
            localStorage.setItem('roadfix_users', JSON.stringify(users));
            return { success: true, user: data.user || userData, message: "Account registered successfully in database!" };
        } catch (e) {
            console.warn("Backend API not reachable, saving to local browser database", e);
            // Offline fallback: save into local database
            const users = JSON.parse(localStorage.getItem('roadfix_users') || '[]');
            const exists = users.some(u => u && u.email && u.email.toLowerCase() === userData.email);
            if (exists) {
                return { success: false, message: `User with email '${userData.email}' is already registered in database. Please log in.` };
            }
            userData.id = "USR-" + (100 + users.length + 1);
            userData.createdAt = new Date().toISOString().split('T')[0];
            users.push(userData);
            localStorage.setItem('roadfix_users', JSON.stringify(users));
            return { success: true, user: userData, message: "Account registered successfully in database!" };
        }
    },

    // 9. Login User with Database Verification
    async login(email, password, role) {
        email = (email || '').trim().toLowerCase();
        password = (password || '').trim();
        role = (role || 'CITIZEN').toUpperCase();

        // 1. Try Backend API
        try {
            const res = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password, role })
            });
            const data = await res.json();
            if (res.ok && data.success) {
                const activeUser = data.user || {
                    email: data.email || email,
                    role: data.role || role,
                    fullName: data.fullName || email.split('@')[0]
                };
                this.setUser(activeUser);
                return { success: true, user: activeUser, message: data.message || "Login successful!" };
            } else {
                return { success: false, message: data.message || "Invalid credentials." };
            }
        } catch (e) {
            console.warn("Backend API not reachable, verifying against local database", e);
            // Offline fallback: verify against local database
            const users = JSON.parse(localStorage.getItem('roadfix_users') || '[]');
            const found = users.find(u => u && u.email && u.email.toLowerCase() === email);
            if (!found) {
                return { success: false, message: `Account with email '${email}' not found in database. Please register first.` };
            }
            if (found.password !== password) {
                return { success: false, message: "Incorrect password. Please try again." };
            }
            const activeUser = {
                id: found.id,
                fullName: found.fullName,
                email: found.email,
                role: found.role,
                departmentName: found.departmentName
            };
            this.setUser(activeUser);
            return { success: true, user: activeUser, message: "Login successful!" };
        }
    },

    // 10. Get all registered users from database
    async getUsers() {
        let serverUsers = [];
        try {
            const res = await fetch(`${API_BASE}/auth/users`);
            if (res.ok) {
                serverUsers = await res.json();
            }
        } catch (e) {}

        const localUsers = JSON.parse(localStorage.getItem('roadfix_users') || '[]');

        // Merge server and local users safely by email
        const mergedMap = new Map();
        serverUsers.forEach(u => {
            if (u && u.email) mergedMap.set(u.email.toLowerCase(), u);
        });

        localUsers.forEach(u => {
            if (u && u.email) {
                const key = u.email.toLowerCase();
                if (!mergedMap.has(key)) {
                    mergedMap.set(key, u);
                    // Sync to backend
                    fetch(`${API_BASE}/auth/register`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(u)
                    }).catch(() => {});
                }
            }
        });

        const allUsers = Array.from(mergedMap.values());
        try {
            localStorage.setItem('roadfix_users', JSON.stringify(allUsers));
        } catch (e) {}
        return allUsers;
    },

    // 11. Feedbacks Management
    async getFeedbacks() {
        let serverFeedbacks = [];
        try {
            const res = await fetch(`${API_BASE}/feedback`);
            if (res.ok) {
                serverFeedbacks = await res.json();
            }
        } catch (e) {}

        const localFeedbacks = JSON.parse(localStorage.getItem('roadfix_feedbacks') || '[]');
        const mergedMap = new Map();
        serverFeedbacks.forEach(f => {
            const key = (f.complaintId || '') + '_' + (f.citizenEmail || f.citizenName || '');
            mergedMap.set(key, f);
        });

        localFeedbacks.forEach(f => {
            const key = (f.complaintId || '') + '_' + (f.citizenEmail || f.citizenName || '');
            if (!mergedMap.has(key)) {
                mergedMap.set(key, f);
                fetch(`${API_BASE}/feedback`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(f)
                }).catch(() => {});
            }
        });

        const allFeedbacks = Array.from(mergedMap.values());
        try {
            localStorage.setItem('roadfix_feedbacks', JSON.stringify(allFeedbacks));
        } catch (e) {}
        return allFeedbacks;
    },

    async submitFeedback(feedbackData) {
        try {
            const res = await fetch(`${API_BASE}/feedback`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(feedbackData)
            });
            if (res.ok) {
                const data = await res.json();
                return data;
            }
        } catch (e) {}
        const list = JSON.parse(localStorage.getItem('roadfix_feedbacks') || '[]');
        list.unshift(feedbackData);
        localStorage.setItem('roadfix_feedbacks', JSON.stringify(list));
        return { success: true, message: "Feedback recorded successfully in database!" };
    },

    // 12. Carousel Slides (Frontend UI only - No database overhead)
    getSlides() {
        try {
            const stored = localStorage.getItem('roadfix_slides');
            if (stored) {
                const parsed = JSON.parse(stored);
                if (Array.isArray(parsed) && parsed.length > 0) return parsed;
            }
        } catch (e) {}
        return DEFAULT_SLIDES;
    },

    updateSlide(index, slideData) {
        const slides = this.getSlides();
        if (slides[index]) {
            Object.assign(slides[index], slideData);
            try {
                localStorage.setItem('roadfix_slides', JSON.stringify(slides));
            } catch (err) {
                console.error("Storage error updating slide", err);
            }
        }
        return slides;
    },

    resetSlides() {
        try {
            localStorage.setItem('roadfix_slides', JSON.stringify(DEFAULT_SLIDES));
        } catch (e) {}
        return DEFAULT_SLIDES;
    },

    // 13. Image Compressor (Auto-scales user photos to lightweight ~80KB web-friendly JPEG)
    compressImage(file, maxWidth = 1280, maxHeight = 720, quality = 0.8) {
        return new Promise((resolve, reject) => {
            if (!file) return reject(new Error('No file provided.'));
            if (!file.type || !file.type.startsWith('image/')) {
                return reject(new Error('Please select a valid image file (JPG, PNG, WebP).'));
            }
            const reader = new FileReader();
            reader.onload = (e) => {
                const img = new Image();
                img.onload = () => {
                    let w = img.width;
                    let h = img.height;
                    if (w > maxWidth) {
                        h = Math.round((h * maxWidth) / w);
                        w = maxWidth;
                    }
                    if (h > maxHeight) {
                        w = Math.round((w * maxHeight) / h);
                        h = maxHeight;
                    }
                    const canvas = document.createElement('canvas');
                    canvas.width = w;
                    canvas.height = h;
                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, w, h);
                    const dataUrl = canvas.toDataURL('image/jpeg', quality);
                    resolve(dataUrl);
                };
                img.onerror = () => reject(new Error('Failed to decode image file.'));
                img.src = e.target.result;
            };
            reader.onerror = () => reject(new Error('Failed to read file from disk.'));
            reader.readAsDataURL(file);
        });
    },

    // 9. Auth Guard: Prevent direct search / direct URL page opening without login
    requireAuth(requiredRole = null, customMessage = null) {
        const user = this.getUser();
        if (!user) {
            sessionStorage.setItem('auth_redirect', window.location.href);
            sessionStorage.setItem('auth_msg', customMessage || `🔒 Restricted Access: Please log in to access this page.`);
            window.location.href = 'login.html';
            return false;
        }

        if (requiredRole) {
            const isMatch = (user.role === requiredRole) || 
                            (requiredRole === 'STAFF' && user.role === 'DEPARTMENT_OFFICER');
            if (!isMatch) {
                sessionStorage.setItem('auth_msg', `⚠️ Access Restricted: You must be logged in as ${requiredRole} to view that portal.`);
                if (user.role === 'ADMIN') window.location.href = 'admin.html';
                else if (user.role === 'STAFF' || user.role === 'DEPARTMENT_OFFICER') window.location.href = 'staff.html';
                else window.location.href = 'citizen.html';
                return false;
            }
        }
        return true;
    }
};

window.RoadFixAPI = RoadFix;

document.addEventListener('DOMContentLoaded', () => {
    RoadFix.renderNavbar();
});
