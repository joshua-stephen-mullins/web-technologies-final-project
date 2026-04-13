package team.projectpulse.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Owner: Oscar (Person 2)
public interface TeamRepository extends JpaRepository<Team, Integer>, JpaSpecificationExecutor<Team> {
    // TODO: existsByName(String name): boolean
}
