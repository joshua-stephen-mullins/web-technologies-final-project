package team.projectpulse.activity.dto;

// Owner: Oscar (Person 2)
public record ActivityDto(Integer id, Integer studentId, String studentName, Integer weekId,
                          String category, String description, Double plannedHours, Double actualHours, String status) {}
