package team.projectpulse.rubric;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RubricRepository extends JpaRepository<Rubric, Integer> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Integer id);

    @Query("SELECT COUNT(s) FROM Section s WHERE s.rubric.id = :rubricId")
    long countSectionsUsingRubric(@Param("rubricId") Integer rubricId);
}
