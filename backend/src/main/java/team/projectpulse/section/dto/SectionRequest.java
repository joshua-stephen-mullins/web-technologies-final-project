package team.projectpulse.section.dto;

// Owner: Josh (Person 1)
// Request body for UC-4: Create section and UC-5: Edit section
public record SectionRequest(String name, String startDate, String endDate, Integer rubricId) {}
