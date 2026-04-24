package team.projectpulse.team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

// Owner: Oscar (Person 2)
public interface TeamRepository extends JpaRepository<Team, Integer>, JpaSpecificationExecutor<Team> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);
    List<Team> findBySectionId(Integer sectionId);
}
