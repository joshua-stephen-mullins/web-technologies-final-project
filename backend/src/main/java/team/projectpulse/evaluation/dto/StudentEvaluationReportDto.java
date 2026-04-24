package team.projectpulse.evaluation.dto;

import java.util.List;

// Owner: Whitney (Person 3) — UC-33
public record StudentEvaluationReportDto(
        String studentName,
        List<WeekReportDto> weeks
) {
    // One entry per week in the requested range
    public record WeekReportDto(
            String weekLabel,
            double grade,
            double maxGrade,
            List<EvaluationDetailDto> evaluations
    ) {}

    // One entry per evaluator who evaluated this student that week
    public record EvaluationDetailDto(
            String evaluatorName,
            String publicComments,
            String privateComments,
            List<CriterionScoreDto> criterionScores
    ) {}

    public record CriterionScoreDto(
            String criterionName,
            String criterionDescription,
            double score,
            double maxScore
    ) {}
}
