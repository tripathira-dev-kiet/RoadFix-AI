package com.roadfix;

import org.springframework.data.annotation.Id;

public class Complaint {
    @Id
    private String id;
    private String title;
    private String category;
    private String location;
    private String severity;
    private String description;
    private String status;
    private String department;
    private String photoUrl;
    private String aiDetected;
    private String aiConfidence;
    private String aiHazard;
    private String repairPhotoUrl;
    private String repairNotes;
    private String date;

    public Complaint() {}

    public Complaint(String id, String title, String category, String location, String severity, String description, String status, String department, String photoUrl, String aiDetected, String aiConfidence, String aiHazard, String date) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.location = location;
        this.severity = severity;
        this.description = description;
        this.status = status;
        this.department = department;
        this.photoUrl = photoUrl;
        this.aiDetected = aiDetected;
        this.aiConfidence = aiConfidence;
        this.aiHazard = aiHazard;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getAiDetected() { return aiDetected; }
    public void setAiDetected(String aiDetected) { this.aiDetected = aiDetected; }

    public String getAiConfidence() { return aiConfidence; }
    public void setAiConfidence(String aiConfidence) { this.aiConfidence = aiConfidence; }

    public String getAiHazard() { return aiHazard; }
    public void setAiHazard(String aiHazard) { this.aiHazard = aiHazard; }

    public String getRepairPhotoUrl() { return repairPhotoUrl; }
    public void setRepairPhotoUrl(String repairPhotoUrl) { this.repairPhotoUrl = repairPhotoUrl; }

    public String getRepairNotes() { return repairNotes; }
    public void setRepairNotes(String repairNotes) { this.repairNotes = repairNotes; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
