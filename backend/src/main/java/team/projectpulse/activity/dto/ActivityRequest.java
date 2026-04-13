package team.projectpulse.activity.dto;

// Owner: Oscar (Person 2)
// Request body for UC-27: create/edit activity
public record ActivityRequest(Integer weekId, String category, String description,
                               Double plannedHours, Double actualHours, String status) {}
