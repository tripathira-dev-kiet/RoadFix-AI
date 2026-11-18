# 🚧 RoadFix AI — Smart Civic Issue Reporting & Municipal Tracking Platform

> **Academic Full-Stack Project | Master of Computer Applications (MCA) | KIET Group of Institutions**

RoadFix AI is a modern, lightweight full-stack municipal management and civic engagement platform. It empowers citizens to report road hazards (potholes, overflowing stormwater drains, blackout streetlights, garbage dumps, pipeline leaks) with GPS coordinates and photos, provides AI hazard assessment, dispatches tasks to municipal field departments, tracks resolution progress through a 5-stage timeline, and captures post-repair photographic proof with citizen satisfaction ratings.

---

## 📸 System Architecture & Highlights

```
+-----------------------------------------------------------------------------------+
|                                 CLIENT TIER                                       |
|  Responsive HTML5 / Modern CSS3 / Vanilla ES6 JavaScript (Fetch API)              |
|  - index.html        : Landing page with real-time operations stats & quick track |
|  - report.html       : Photo upload, HTML5 GPS geolocation & AI Hazard Analysis   |
|  - track.html        : 5-Stage visual progress timeline & before/after proof view |
|  - complaints.html   : Community issue explorer with category & status filters    |
|  - admin.html        : Municipal command center, photo inspector & crew dispatch  |
|  - staff.html        : Field operations panel with repair proof photo upload      |
|  - feedback.html     : 5-Star citizen satisfaction rating & quality review        |
|  - login/register    : Role-based authentication (Citizen, Admin, Staff)          |
+-----------------------------------------+-----------------------------------------+
                                          | HTTP REST (JSON)
                                          v
+-----------------------------------------------------------------------------------+
|                            APPLICATION / SERVICE TIER                             |
|  Java (Spring Boot 3.3.3) | Embedded Tomcat | Port 8080                           |
|  - RoadFixApplication.java : Spring Boot main runner & static resource host       |
|  - ApiController.java      : REST API endpoints for complaints, stats & feedback  |
|  - Complaint.java          : Data model with AI detection & repair proof fields   |
+-----------------------------------------------------------------------------------+
```

---

## 🛠️ Technology Stack

| Layer | Technologies Used |
|---|---|
| **Frontend** | HTML5, CSS3 (Responsive Design System), JavaScript (ES6+), Fetch API, LocalStorage (Offline Fallback) |
| **Backend** | Java 21/24, Spring Boot 3.3.3, Embedded Tomcat Server (Port 8080) |
| **AI Assessment** | Automated vision assessment for pothole severity, hazard score & dispatch classification |
| **Build & Tooling** | Maven Wrapper (`mvnw.cmd`) — No separate Maven installation required |

---

## 📁 Clean Project Directory Structure

```
RFIX/
├── .gitignore                      # Git exclusion rules (target, logs, IDE files)
├── README.md                       # Complete setup & project documentation
├── Frontend/                       # Responsive Client Application
│   ├── index.html                  # Landing page with live metrics
│   ├── citizen.html                # Citizen Dashboard (My reports, live tracking, stats)
│   ├── login.html                  # Role-based login with 1-click demo accounts
│   ├── register.html               # New user registration portal
│   ├── report.html                 # Citizen complaint submission with photo & AI
│   ├── track.html                  # 5-Stage visual timeline & repair verification
│   ├── complaints.html             # Public community issues explorer
│   ├── admin.html                  # Administrative command dashboard & dispatch
│   ├── staff.html                  # Field staff panel & post-repair proof upload
│   ├── feedback.html               # Citizen satisfaction rating & reviews
│   ├── css/
│   │   └── style.css               # Clean municipal design system & responsive styling
│   └── js/
│       └── api.js                  # Global API client, AI engine & session storage
└── Backend/                        # Lightweight Spring Boot Backend
    ├── pom.xml                     # Maven project configuration (Java 21 bytecode target)
    ├── mvnw / mvnw.cmd             # Maven Wrapper scripts
    ├── .mvn/wrapper/               # Maven wrapper binaries
    └── src/main/
        ├── resources/
        │   ├── application.properties # Server port 8080 configuration
        │   └── static/             # Bundled frontend for single-port serving
        └── java/com/roadfix/
            ├── RoadFixApplication.java # Spring Boot application entry point
            ├── ApiController.java      # REST endpoints (/api/complaints, /api/stats, etc.)
            └── Complaint.java          # Core data model entity
```

---

## 🚀 How to Run the Project (चलने का तरीका — 1 Click!)

You have **two super simple options**:

### Option 1: 1-Click Full-Stack Run (सबसे आसान तरीका!)
1. Root folder mein **`run.bat`** par double-click karein!
2. Ye automatically Spring Boot server start karega aur aapke web browser mein `http://localhost:8080/` open kar dega.
- Terminal command agar chalana ho:
  ```powershell
  .\run.bat
  ```

---

### Option 2: 1-Click Direct Browser Run (No Server Required)
1. Root folder mein **`run_frontend.bat`** par double-click karein (ya `Frontend/index.html` open karein)!
2. Turant aapke browser mein RoadFix AI open ho jayega. Built-in hybrid engine ki wajah se sabhi features (Citizen Dashboard, Issue Reporting, 5-Stage Live Tracking, Admin Command, Staff Operations, Feedback) bina kisi server ke offline chalenge!

---

## 👥 Demo Login Credentials (टेस्ट करने के लिए अकाउंट)

| Role | Username | Password | Access & Features |
|---|---|---|---|
| **Citizen** | `citizen` | `citizen123` | Submit complaints, view AI hazard assessment, track live status, submit feedback |
| **Field Staff** | `staff` | `staff123` | View assigned department work orders, inspect citizen photos, start work, upload repair proof |
| **Administrator** | `admin` | `admin123` | Full municipal dashboard, inspect citizen photos & AI scores, assign departments, update statuses |

*(Note: On the login page, you can also click the quick one-click demo login buttons!)*

---

## 🌟 Key Features Implemented

1. **Citizen Complaint Reporting**:
   - Photo upload (with live browser preview)
   - Category selection (Pothole, Drainage, Street Light, Garbage, Water Leakage, Road Damage)
   - Automatic realistic AI Assessment (Surface severity, Confidence score, Municipal hazard alert)
   - Auto-generated unique Tracking ID (e.g. `RF-20261118-001`)

2. **Live Tracking & 5-Stage Timeline**:
   - Submitted ➔ Reviewed ➔ Assigned ➔ In Progress ➔ Resolved
   - Citizen uploaded photo displayed alongside issue details
   - Field staff post-repair proof photo and work notes visible once resolved

3. **Field Staff Operations Portal**:
   - Filter work orders by department (Roads & Infrastructure, Sanitation, Electrical, Water Supply)
   - View citizen reported photos and AI hazard notes
   - Mark work as "In Progress"
   - Upload repair completion proof photo and notes to resolve the issue

4. **Municipal Admin Command Center**:
   - Real-time metrics counters (Total, Pending, Assigned, In Progress, Resolved, Rating)
   - Active complaints registry with photo thumbnails
   - Inspection modal showing citizen photo, AI Vision score, and repair proof
   - Department assignment and status update controls
   - Citizen quality and speed feedback reviews table

5. **Security & Privacy**:
   - No hardcoded passwords, tokens, or sensitive API keys
   - Clean `.gitignore` excluding build files and secrets
