package team.projectpulse.evaluation.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// Owner: Whitney (Person 3) — request body for POST /api/evaluations/batch (UC-28)
public record BatchEvaluationRequest(
        Integer weekId,
        List<EvaluationEntry> evaluations
) {
    public record EvaluationEntry(
            Integer evaluateeId,
            Map<Integer, BigDecimal> scores,
            String publicComments,
            String privateComments
    ) {}
}
