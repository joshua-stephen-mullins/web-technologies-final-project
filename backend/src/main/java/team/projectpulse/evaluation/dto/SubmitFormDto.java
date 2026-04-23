package team.projectpulse.evaluation.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Owner: Whitney (Person 3) — response for GET /api/evaluations/submit-form (UC-28)
public record SubmitFormDto(
        WeekInfo week,
        List<TeammateInfo> teammates,
        List<CriterionInfo> criteria,
        Map<Integer, ExistingEvaluation> existingEvaluations
) {
    public record WeekInfo(Integer id, Integer weekNumber, LocalDate startDate, LocalDate endDate) {}

    public record TeammateInfo(Integer id, String firstName, String lastName) {}

    public record CriterionInfo(Integer id, String name, String description, BigDecimal maxScore) {}

    public record ExistingEvaluation(
            String publicComments,
            String privateComments,
            Map<Integer, BigDecimal> scores
    ) {}
}
