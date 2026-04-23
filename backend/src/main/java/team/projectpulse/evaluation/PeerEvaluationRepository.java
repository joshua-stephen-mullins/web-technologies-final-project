package team.projectpulse.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Owner: Whitney (Person 3)
public interface PeerEvaluationRepository extends JpaRepository<PeerEvaluation, Integer> {
    Optional<PeerEvaluation> findByEvaluator_IdAndEvaluatee_IdAndWeek_Id(Integer evaluatorId, Integer evaluateeId, Integer weekId);
    List<PeerEvaluation> findByEvaluator_IdAndWeek_Id(Integer evaluatorId, Integer weekId);
}
