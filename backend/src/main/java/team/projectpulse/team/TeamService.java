package team.projectpulse.team;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.activity.ActivityRepository;
import team.projectpulse.section.Section;
import team.projectpulse.section.SectionRepository;
import team.projectpulse.system.exception.ObjectAlreadyExistsException;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.team.dto.TeamRequest;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.PeerEvaluationUserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Owner: Oscar (Person 2)
// Related UCs: UC-7,8,9,10,12,13,14
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final SectionRepository sectionRepository;
    private final PeerEvaluationUserRepository userRepository;
    private final ActivityRepository activityRepository;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public TeamService(TeamRepository teamRepository,
                       SectionRepository sectionRepository,
                       PeerEvaluationUserRepository userRepository,
                       ActivityRepository activityRepository,
                       JavaMailSender mailSender) {
        this.teamRepository = teamRepository;
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
        this.mailSender = mailSender;
    }

    // UC-7: Find teams with optional search filters
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<Team> findAllTeams(String name, Integer sectionId) {
        Specification<Team> spec = (root, query, cb) -> cb.conjunction();
        if (name != null && !name.isBlank()) {
            spec = spec.and(TeamSpecs.hasName(name));
        }
        if (sectionId != null) {
            spec = spec.and(TeamSpecs.hasSectionId(sectionId));
        }
        return teamRepository.findAll(spec);
    }

    // UC-8: View team details
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public Team findTeamById(Integer id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("team", id));
    }

    // UC-9: Create team
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Team createTeam(TeamRequest request) {
        if (teamRepository.existsByName(request.name())) {
            throw new ObjectAlreadyExistsException("A team named '" + request.name() + "' already exists");
        }
        Section section = sectionRepository.findById(request.sectionId())
                .orElseThrow(() -> new ObjectNotFoundException("section", request.sectionId()));

        Team team = new Team();
        team.setName(request.name());
        team.setDescription(request.description());
        team.setWebsiteUrl(request.websiteUrl());
        team.setSection(section);
        return teamRepository.save(team);
    }

    // UC-10: Edit team
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Team updateTeam(Integer id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("team", id));

        if (!team.getName().equals(request.name()) && teamRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ObjectAlreadyExistsException("A team named '" + request.name() + "' already exists");
        }

        team.setName(request.name());
        team.setDescription(request.description());
        team.setWebsiteUrl(request.websiteUrl());
        return teamRepository.save(team);
    }

    // UC-12: Assign students to a team
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Team assignStudents(Integer teamId, List<Integer> studentIds) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundException("team", teamId));

        List<PeerEvaluationUser> users = userRepository.findAllById(studentIds);

        Set<Integer> existingIds = team.getStudents().stream()
                .map(PeerEvaluationUser::getId)
                .collect(Collectors.toSet());

        List<PeerEvaluationUser> newStudents = users.stream()
                .filter(u -> !existingIds.contains(u.getId()))
                .toList();

        team.getStudents().addAll(newStudents);
        Team saved = teamRepository.save(team);

        for (PeerEvaluationUser student : newStudents) {
            notifyStudentOfAssignment(student, team);
        }

        return saved;
    }

    // UC-13: Remove student from team
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Team removeStudent(Integer teamId, Integer studentId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundException("team", teamId));

        PeerEvaluationUser removed = team.getStudents().stream()
                .filter(u -> u.getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("student", studentId));

        team.getStudents().remove(removed);
        Team saved = teamRepository.save(team);

        notifyStudentOfRemoval(removed, team);
        return saved;
    }

    // UC-14: Delete team (removes students from team, deletes activities)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteTeam(Integer teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundException("team", teamId));

        List<PeerEvaluationUser> studentsToNotify = List.copyOf(team.getStudents());
        List<Integer> studentIds = studentsToNotify.stream().map(PeerEvaluationUser::getId).toList();

        // Delete activities for all team students
        if (!studentIds.isEmpty()) {
            activityRepository.deleteByStudentIdIn(studentIds);
        }

        teamRepository.delete(team);

        for (PeerEvaluationUser student : studentsToNotify) {
            notifyStudentOfTeamDeletion(student, team.getName());
        }
    }

    private void notifyStudentOfAssignment(PeerEvaluationUser student, Team team) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getUsername());
            message.setSubject("Team Assignment – Project Pulse");
            message.setText("Hello " + student.getFirstName() + ",\n\n"
                    + "You have been assigned to team \"" + team.getName() + "\".\n\n"
                    + "Log in to view your team: " + frontendUrl + "\n");
            mailSender.send(message);
        } catch (Exception ignored) {}
    }

    private void notifyStudentOfRemoval(PeerEvaluationUser student, Team team) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getUsername());
            message.setSubject("Team Removal – Project Pulse");
            message.setText("Hello " + student.getFirstName() + ",\n\n"
                    + "You have been removed from team \"" + team.getName() + "\".\n\n"
                    + "Please contact your instructor if you have questions.");
            mailSender.send(message);
        } catch (Exception ignored) {}
    }

    private void notifyStudentOfTeamDeletion(PeerEvaluationUser student, String teamName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(student.getUsername());
            message.setSubject("Team Deleted – Project Pulse");
            message.setText("Hello " + student.getFirstName() + ",\n\n"
                    + "Team \"" + teamName + "\" has been deleted. Please contact your admin.");
            mailSender.send(message);
        } catch (Exception ignored) {}
    }
}
