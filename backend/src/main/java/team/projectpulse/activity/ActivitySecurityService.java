package team.projectpulse.activity;

import org.springframework.stereotype.Service;

// Owner: Oscar (Person 2)
// Ensures students can only modify their own activities
@Service("activitySecurity")
public class ActivitySecurityService {

    private final ActivityRepository activityRepository;

    public ActivitySecurityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public boolean isActivityOwner(Integer activityId, String username) {
        return activityRepository.findById(activityId)
                .map(a -> a.getStudent() != null && a.getStudent().getUsername().equals(username))
                .orElse(false);
    }
}
