package team.projectpulse.instructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import team.projectpulse.user.userinvitation.UserInvitationService;

import java.util.List;

// Owner: Whitney (Person 3)
@Service
public class InstructorService {

    private final UserInvitationService invitationService;

    public InstructorService(UserInvitationService invitationService) {
        this.invitationService = invitationService;
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
}