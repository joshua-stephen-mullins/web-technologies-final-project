package team.projectpulse.rubric.dto;

import java.math.BigDecimal;

public record CriterionDto(Integer id, String name, String description, BigDecimal maxScore, Integer sortOrder) {}
