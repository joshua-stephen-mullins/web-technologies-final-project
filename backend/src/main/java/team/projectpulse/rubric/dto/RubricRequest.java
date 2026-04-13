package team.projectpulse.rubric.dto;

import java.util.List;

// Owner: Josh (Person 1)
// Request body for UC-1: Create rubric
public record RubricRequest(String name, List<CriterionDto> criteria) {}
