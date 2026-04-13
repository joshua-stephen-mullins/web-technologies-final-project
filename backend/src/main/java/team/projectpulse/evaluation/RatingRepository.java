package team.projectpulse.evaluation;

import org.springframework.data.jpa.repository.JpaRepository;

// Owner: Whitey (Person 3)
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    // TODO: findByPeerEvaluationId(Integer peerEvaluationId): List<Rating>
}
