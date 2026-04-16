package team.projectpulse.evaluation.dto;

import java.util.List;

// Owner: Whitney (Person 3)
// Outbound report DTO for UC-29, UC-31, UC-33
public record EvaluationReportDto(String weekLabel, List<StudentAverageDto> studentAverages,
                                   List<String> nonSubmitters) {
    public record StudentAverageDto(Integer studentId, String studentName,
                                    Double averageGrade, Double maxGrade,
                                    List<CriterionAverageDto> criterionAverages) {}
    public record CriterionAverageDto(String criterionName, Double averageScore, Double maxScore) {}
}
