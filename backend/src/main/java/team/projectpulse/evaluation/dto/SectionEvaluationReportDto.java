package team.projectpulse.evaluation.dto;

import java.util.List;

// Owner: Whitney (Person 3) — UC-31
public record SectionEvaluationReportDto(
        String weekLabel,
        List<StudentReportDto> students,
        List<String> nonSubmitters
) {
    // One entry per student, sorted by last name
    public record StudentReportDto(
            Integer studentId,
            String studentName,
            double grade,
            double maxGrade,
            List<EvaluationDetailDto> evaluations
    ) {}

    // One entry per evaluator who evaluated this student
    public record EvaluationDetailDto(
            String evaluatorName,
            String publicComments,
            String privateComments,
            List<CriterionScoreDto> criterionScores
    ) {}

    // Per-criterion score given by one evaluator
    public record CriterionScoreDto(
            String criterionName,
            String criterionDescription,
            double score,
            double maxScore
    ) {}
}
