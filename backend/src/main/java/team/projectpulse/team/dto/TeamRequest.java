package team.projectpulse.team.dto;

// Owner: Oscar (Person 2)
// Request body for UC-9: Create team and UC-10: Edit team
public record TeamRequest(String name, String description, String websiteUrl, Integer sectionId) {}
