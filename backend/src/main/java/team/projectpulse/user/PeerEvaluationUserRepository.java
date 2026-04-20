package team.projectpulse.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PeerEvaluationUserRepository extends JpaRepository<PeerEvaluationUser, Integer> {
    Optional<PeerEvaluationUser> findByUsername(String username);
}
