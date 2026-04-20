package team.projectpulse.instructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamRepository;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.PeerEvaluationUserRepository;
import team.projectpulse.user.userinvitation.UserInvitationService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Owner: Whitney (Person 3)
@Service
public class InstructorService {

    private final UserInvitationService invitationService;
    private final InstructorRepository instructorRepository;
    private final TeamRepository teamRepository;
    private final PeerEvaluationUserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public InstructorService(UserInvitationService invitationService,
                             InstructorRepository instructorRepository,
                             TeamRepository teamRepository,
                             PeerEvaluationUserRepository userRepository,
                             JavaMailSender mailSender) {
        this.invitationService = invitationService;
        this.instructorRepository = instructorRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    // UC-18: Invite instructors by email
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public int inviteInstructors(List<String> emails) {
        int count = 0;
        for (String email : emails) {
            String trimmed = email.trim();
            if (!trimmed.isEmpty()) {
                invitationService.sendInvitation(trimmed, "ROLE_INSTRUCTOR", null);
                count++;
            }
        }
        return count;
    }

    // UC-21: List all instructors
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<Instructor> findAllInstructors() {
        return instructorRepository.findAll(InstructorSpecs.hasRole("ROLE_INSTRUCTOR"));
    }

    // UC-19: Get instructors currently assigned to a team
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<PeerEvaluationUser> findTeamInstructors(Integer teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundException("team", teamId));
        return team.getInstructors();
    }

    // UC-19: Assign instructors to a team; notifies newly assigned instructors
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public List<PeerEvaluationUser> assignInstructorsToTeam(Integer teamId, List<Integer> instructorIds) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundException("team", teamId));

        List<Instructor> instructors = instructorRepository.findAllById(instructorIds);

        Set<Integer> existingIds = team.getInstructors().stream()
                .map(PeerEvaluationUser::getId)
                .collect(Collectors.toSet());

        List<Instructor> newlyAssigned = instructors.stream()
                .filter(i -> !existingIds.contains(i.getId()))
                .toList();

        List<PeerEvaluationUser> newUsers = userRepository.findAllById(
                newlyAssigned.stream().map(Instructor::getId).toList()
        );
        team.getInstructors().addAll(newUsers);
        teamRepository.save(team);

        for (Instructor instructor : newlyAssigned) {
            notifyInstructorOfAssignment(instructor, team);
        }

        return team.getInstructors();
    }

    // UC-20: Remove an instructor from a team
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void removeInstructorFromTeam(Integer teamId, Integer instructorId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundException("team", teamId));
        team.getInstructors().removeIf(u -> u.getId().equals(instructorId));
        teamRepository.save(team);
    }

    private void notifyInstructorOfAssignment(Instructor instructor, Team team) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(instructor.getUsername());
        message.setSubject("Team Assignment – Project Pulse");
        message.setText("Hello " + instructor.getFirstName() + ",\n\n"
                + "You have been assigned to supervise team \"" + team.getName() + "\".\n\n"
                + "Log in to view your team: " + frontendUrl + "\n");
        mailSender.send(message);
    }
}
