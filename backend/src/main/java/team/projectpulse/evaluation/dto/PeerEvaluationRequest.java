package team.projectpulse.evaluation.dto;

import java.util.List;

// Owner: Whitey (Person 3)
// Request body for UC-28: submit peer evaluation
// ratings is a list of {criterionId, score} pairs — one per criterion in the rubric
public record PeerEvaluationRequest(Integer evaluateeId, Integer weekId,
                                     String publicComments, String privateComments,
                                     List<RatingRequest> ratings) {
    public record RatingRequest(Integer criterionId, Double score) {}
}
