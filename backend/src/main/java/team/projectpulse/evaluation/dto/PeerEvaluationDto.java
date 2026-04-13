package team.projectpulse.evaluation.dto;

// Owner: Whitey (Person 3)
public record PeerEvaluationDto(Integer id, Integer evaluatorId, Integer evaluateeId,
                                Integer weekId, String publicComments, String privateComments) {}
