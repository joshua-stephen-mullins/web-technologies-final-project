package team.projectpulse.instructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.instructor.dto.InstructorDetailDto;
import team.projectpulse.instructor.dto.InstructorDto;
import team.projectpulse.section.Section;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamRepository;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.PeerEvaluationUserRepository;
import team.projectpulse.user.userinvitation.UserInvitationService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    // UC-21: Find instructors matching optional search criteria, sorted by academic year desc then last name asc
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @Transactional(readOnly = true)
    public List<InstructorDto> findInstructors(String firstName, String lastName, String teamName, Boolean enabled) {
        Specification<Instructor> spec = InstructorSpecs.hasRole("ROLE_INSTRUCTOR");
        if (firstName != null && !firstName.isBlank()) spec = spec.and(InstructorSpecs.hasFirstName(firstName));
        if (lastName != null && !lastName.isBlank()) spec = spec.and(InstructorSpecs.hasLastName(lastName));
        if (teamName != null && !teamName.isBlank()) spec = spec.and(InstructorSpecs.hasTeamName(teamName));
        if (enabled != null) spec = spec.and(InstructorSpecs.isEnabled(enabled));

        List<Instructor> instructors = new ArrayList<>(instructorRepository.findAll(spec));
        instructors.sort(Comparator
                .comparingInt(this::maxSectionYear).reversed()
                .thenComparing(Instructor::getLastName));

        return instructors.stream().map(this::toInstructorDto).toList();
    }

    private int maxSectionYear(Instructor instructor) {
        return instructor.getTeams().stream()
                .map(Team::getSection)
                .filter(Objects::nonNull)
                .mapToInt(section -> section.getStartDate().getYear())
                .max()
                .orElse(0);
    }

    private InstructorDto toInstructorDto(Instructor instructor) {
        List<String> teamNames = instructor.getTeams().stream()
                .map(Team::getName)
                .sorted()
                .toList();
        return new InstructorDto(
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getUsername(),
                instructor.isEnabled(),
                teamNames
        );
    }

    // UC-22: View details of a single instructor with teams grouped by section
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @Transactional(readOnly = true)
    public InstructorDetailDto findInstructor(Integer instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .filter(i -> i.getRoles().contains("ROLE_INSTRUCTOR"))
                .orElseThrow(() -> new ObjectNotFoundException("instructor", instructorId));

        Map<Integer, Section> sectionMap = new LinkedHashMap<>();
        Map<Integer, List<Team>> teamsBySectionId = new LinkedHashMap<>();
        for (Team team : instructor.getTeams()) {
            Section section = team.getSection();
            sectionMap.put(section.getId(), section);
            teamsBySectionId.computeIfAbsent(section.getId(), k -> new ArrayList<>()).add(team);
        }

        List<InstructorDetailDto.SectionTeams> teamsBySection = sectionMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Section>comparingByValue(
                        Comparator.comparing(Section::getStartDate).reversed()
                ))
                .map(entry -> new InstructorDetailDto.SectionTeams(
                        entry.getKey(),
                        entry.getValue().getName(),
                        teamsBySectionId.get(entry.getKey()).stream()
                                .map(t -> new InstructorDetailDto.TeamSummary(t.getId(), t.getName()))
                                .sorted(Comparator.comparing(InstructorDetailDto.TeamSummary::name))
                                .toList()
                ))
                .toList();

        return new InstructorDetailDto(
                instructor.getId(),
                instructor.getFirstName(),
                instructor.getLastName(),
                instructor.getUsername(),
                instructor.isEnabled(),
                teamsBySection
        );
    }

    // UC-23: Deactivate an instructor
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deactivateInstructor(Integer instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .filter(i -> i.getRoles().contains("ROLE_INSTRUCTOR"))
                .orElseThrow(() -> new ObjectNotFoundException("instructor", instructorId));
        instructor.setEnabled(false);
        instructorRepository.save(instructor);
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

    // UC-20: Remove an instructor from a team and notify them
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void removeInstructorFromTeam(Integer teamId, Integer instructorId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ObjectNotFoundException("team", teamId));
        boolean removed = team.getInstructors().removeIf(u -> u.getId().equals(instructorId));
        if (removed) {
            teamRepository.save(team);
            instructorRepository.findById(instructorId)
                    .ifPresent(instructor -> notifyInstructorOfRemoval(instructor, team));
        }
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

    private void notifyInstructorOfRemoval(Instructor instructor, Team team) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(instructor.getUsername());
        message.setSubject("Team Assignment Update – Project Pulse");
        message.setText("Hello " + instructor.getFirstName() + ",\n\n"
                + "You have been removed from team \"" + team.getName() + "\" in Project Pulse.\n\n"
                + "Please contact your administrator if you have any questions.\n");
        mailSender.send(message);
    }
}
