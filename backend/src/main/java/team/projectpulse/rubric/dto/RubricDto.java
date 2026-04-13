package team.projectpulse.rubric.dto;

import java.util.List;

// Owner: Josh (Person 1)
public record RubricDto(Integer id, String name, List<CriterionDto> criteria) {}
