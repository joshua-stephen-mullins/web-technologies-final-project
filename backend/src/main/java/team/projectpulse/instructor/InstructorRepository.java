package team.projectpulse.instructor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

// Owner: Whitney (Person 3)
public interface InstructorRepository extends JpaRepository<Instructor, Integer>, JpaSpecificationExecutor<Instructor> {
    // TODO: Add query methods
}
