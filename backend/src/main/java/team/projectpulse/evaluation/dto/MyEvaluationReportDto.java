package team.projectpulse.evaluation.dto;

import java.util.List;

// Owner: Whitney (Person 3) — UC-29
public record MyEvaluationReportDto(
        String weekLabel,
        String studentName,
        int evaluatorCount,
        List<CriterionAverageDto> criterionAverages,
        List<String> publicComments,
        double averageGrade,
        double maxGrade
) {
    public record CriterionAverageDto(String criterionName, String criterionDescription, double averageScore, double maxScore) {}
}
