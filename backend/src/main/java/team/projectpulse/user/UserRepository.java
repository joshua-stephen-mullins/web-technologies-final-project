package team.projectpulse.user;

import org.springframework.data.jpa.repository.JpaRepository;

// Owner: Josh (Person 1)
public interface UserRepository extends JpaRepository<PeerEvaluationUser, Integer> {
    // TODO: Add query methods
    // findByUsername(String username): Optional<PeerEvaluationUser>
}
