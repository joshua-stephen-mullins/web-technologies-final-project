package team.projectpulse.rubric;

import org.springframework.data.jpa.repository.JpaRepository;

// Owner: Josh (Person 1)
public interface RubricRepository extends JpaRepository<Rubric, Integer> {
    // TODO: existsByName(String name): boolean
}
