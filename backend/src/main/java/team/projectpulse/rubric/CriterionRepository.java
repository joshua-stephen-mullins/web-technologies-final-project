package team.projectpulse.rubric;

import org.springframework.data.jpa.repository.JpaRepository;

// Owner: Josh (Person 1)
public interface CriterionRepository extends JpaRepository<Criterion, Integer> {
    // TODO: findByRubricIdOrderBySortOrder(Integer rubricId): List<Criterion>
}
