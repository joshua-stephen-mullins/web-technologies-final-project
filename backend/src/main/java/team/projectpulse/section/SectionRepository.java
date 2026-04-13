package team.projectpulse.section;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Owner: Josh (Person 1)
public interface SectionRepository extends JpaRepository<Section, Integer>, JpaSpecificationExecutor<Section> {
    // TODO: existsByName(String name): boolean
}
