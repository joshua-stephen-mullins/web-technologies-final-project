package team.projectpulse.student;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.student.dto.StudentDto;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamRepository;
import team.projectpulse.user.userinvitation.UserInvitationService;

import java.util.List;

// Owner: Oscar (Person 2)
@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final TeamRepository teamRepository;
    private final UserInvitationService invitationService;

    public StudentService(StudentRepository studentRepository,
                          TeamRepository teamRepository,
                          UserInvitationService invitationService) {
        this.studentRepository = studentRepository;
        this.teamRepository = teamRepository;
        this.invitationService = invitationService;
    }

    // UC-15: Find students with optional filters
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<StudentDto> findStudents(String firstName, String lastName, String email, Integer teamId) {
        Specification<Student> spec = StudentSpecs.hasRole("ROLE_STUDENT");
        if (firstName != null && !firstName.isBlank()) spec = spec.and(StudentSpecs.hasFirstName(firstName));
        if (lastName != null && !lastName.isBlank()) spec = spec.and(StudentSpecs.hasLastName(lastName));
        if (email != null && !email.isBlank()) spec = spec.and(StudentSpecs.hasEmail(email));
        if (teamId != null) spec = spec.and(StudentSpecs.hasTeamId(teamId));

        return studentRepository.findAll(spec).stream()
                .map(this::toDto)
                .toList();
    }

    // UC-16: View student details
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public StudentDto findStudentById(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("student", id));
        return toDto(student);
    }

    // UC-17: Delete student (physical delete; activities cascade via DB ON DELETE CASCADE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteStudent(Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new ObjectNotFoundException("student", id);
        }
        studentRepository.deleteById(id);
    }

    // UC-11: Invite students by email
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int inviteStudents(List<String> emails, Integer sectionId) {
        int count = 0;
        for (String email : emails) {
            String trimmed = email.trim();
            if (!trimmed.isEmpty()) {
                invitationService.sendInvitation(trimmed, "ROLE_STUDENT", sectionId);
                count++;
            }
        }
        return count;
    }

    private StudentDto toDto(Student student) {
        String teamName = null;
        String sectionName = null;
        if (student.getTeamId() != null) {
            Team team = teamRepository.findById(student.getTeamId()).orElse(null);
            if (team != null) {
                teamName = team.getName();
                sectionName = team.getSection() != null ? team.getSection().getName() : null;
            }
        }
        return new StudentDto(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getUsername(),
                teamName,
                sectionName
        );
    }
}
