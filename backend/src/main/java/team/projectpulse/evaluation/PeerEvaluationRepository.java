package team.projectpulse.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;

// Owner: Whitey (Person 3)
public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Integer> {
    // TODO: findByEvaluateeIdAndWeekId(Integer evaluateeId, Integer weekId): List<PeerEvaluation>
    // TODO: findByEvaluatorIdAndWeekId(Integer evaluatorId, Integer weekId): Optional<PeerEvaluation>
    // TODO: findByWeekIdAndEvaluateeTeamId(Integer weekId, Integer teamId): List<PeerEvaluation>
}
