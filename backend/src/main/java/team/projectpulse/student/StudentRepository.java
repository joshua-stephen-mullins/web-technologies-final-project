package team.projectpulse.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

// Owner: Oscar (Person 2)
public interface StudentRepository extends JpaRepository<Student, Integer>, JpaSpecificationExecutor<Student> {
    Optional<Student> findByUsername(String username);
    boolean existsByUsername(String username);
}
