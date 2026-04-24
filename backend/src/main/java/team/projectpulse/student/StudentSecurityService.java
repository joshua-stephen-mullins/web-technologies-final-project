package team.projectpulse.student;

import org.springframework.stereotype.Service;

// Owner: Oscar (Person 2)
@Service("studentSecurity")
public class StudentSecurityService {

    private final StudentRepository studentRepository;

    public StudentSecurityService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public boolean isCurrentStudent(Integer studentId, String username) {
        return studentRepository.findByUsername(username)
                .map(s -> s.getId().equals(studentId))
                .orElse(false);
    }
}
