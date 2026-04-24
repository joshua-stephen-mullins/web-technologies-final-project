package team.projectpulse.activity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.section.Week;
import team.projectpulse.section.WeekRepository;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.activity.dto.ActivityRequest;
import team.projectpulse.team.Team;
import team.projectpulse.team.TeamRepository;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.PeerEvaluationUserRepository;

import java.math.BigDecimal;
import java.util.List;

// Owner: Oscar (Person 2)
@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final PeerEvaluationUserRepository userRepository;
    private final WeekRepository weekRepository;
    private final TeamRepository teamRepository;

    public ActivityService(ActivityRepository activityRepository,
                           PeerEvaluationUserRepository userRepository,
                           WeekRepository weekRepository,
                           TeamRepository teamRepository) {
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.weekRepository = weekRepository;
        this.teamRepository = teamRepository;
    }

    // UC-27: Get current student's activities for a week
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<Activity> findStudentActivities(Integer weekId, String username) {
        PeerEvaluationUser student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));
        return activityRepository.findByStudentIdAndWeekId(student.getId(), weekId);
    }

    // UC-27: Add activity to WAR
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @Transactional
    public Activity createActivity(ActivityRequest request, String username) {
        PeerEvaluationUser student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));
        Week week = weekRepository.findById(request.weekId())
                .orElseThrow(() -> new ObjectNotFoundException("week", request.weekId()));

        Activity activity = new Activity();
        activity.setStudent(student);
        activity.setWeek(week);
        activity.setCategory(request.category());
        activity.setDescription(request.description());
        activity.setPlannedHours(request.plannedHours() != null ? BigDecimal.valueOf(request.plannedHours()) : null);
        activity.setActualHours(request.actualHours() != null ? BigDecimal.valueOf(request.actualHours()) : null);
        activity.setStatus(request.status());
        return activityRepository.save(activity);
    }

    // UC-27: Edit activity
    @PreAuthorize("@activitySecurity.isActivityOwner(#id, authentication.name) or hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    @Transactional
    public Activity updateActivity(Integer id, ActivityRequest request) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("activity", id));

        activity.setCategory(request.category());
        activity.setDescription(request.description());
        activity.setPlannedHours(request.plannedHours() != null ? BigDecimal.valueOf(request.plannedHours()) : null);
        activity.setActualHours(request.actualHours() != null ? BigDecimal.valueOf(request.actualHours()) : null);
        activity.setStatus(request.status());
        return activityRepository.save(activity);
    }

    // UC-27: Delete activity
    @PreAuthorize("@activitySecurity.isActivityOwner(#id, authentication.name) or hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteActivity(Integer id) {
        if (!activityRepository.existsById(id)) {
            throw new ObjectNotFoundException("activity", id);
        }
        activityRepository.deleteById(id);
    }

    // UC-32: Generate team WAR report for a week
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR', 'ROLE_STUDENT')")
    public List<Activity> generateTeamWARReport(Integer teamId, Integer weekId) {
        return activityRepository.findByTeamIdAndWeekId(teamId, weekId);
    }

    // UC-34: Generate student WAR report over a range of weeks (for Whitney's UC-34)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<Activity> generateStudentWARReport(Integer studentId, List<Integer> weekIds) {
        return activityRepository.findByStudentIdAndWeekIdIn(studentId, weekIds);
    }

    // UC-27 helper: Returns the active weeks for the current student's section
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<Week> getMyWeeks(String username) {
        PeerEvaluationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));
        if (user.getTeamId() == null) {
            throw new IllegalStateException("You are not assigned to a team yet.");
        }
        Team team = teamRepository.findById(user.getTeamId())
                .orElseThrow(() -> new ObjectNotFoundException("team", user.getTeamId()));
        return weekRepository.findBySectionIdOrderByWeekNumber(team.getSection().getId()).stream()
                .filter(Week::isActive)
                .toList();
    }
}
