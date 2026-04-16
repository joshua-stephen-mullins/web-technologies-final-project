package team.projectpulse.rubric;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CriterionRepository extends JpaRepository<Criterion, Integer> {
    List<Criterion> findByRubricIdOrderBySortOrder(Integer rubricId);
}
