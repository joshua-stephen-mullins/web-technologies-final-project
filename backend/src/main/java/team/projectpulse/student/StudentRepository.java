package team.projectpulse.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Owner: Oscar (Person 2)
public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
    // TODO: Add query methods
}
