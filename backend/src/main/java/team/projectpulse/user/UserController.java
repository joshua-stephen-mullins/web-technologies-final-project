package team.projectpulse.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Owner: Josh (Person 1)
// Related UCs: UC-25 (student registration), UC-26 (edit account), UC-30 (instructor registration)
// Endpoints:
//   POST   /api/users/register          - complete registration via invite token
//   GET    /api/users/me                - get current user profile
//   PUT    /api/users/me                - UC-26: edit own account
//   POST   /api/users/forgot-password   - request password reset email
//   POST   /api/users/reset-password    - complete password reset
@RestController
@RequestMapping("/api/users")
public class UserController {
    // TODO: Implement
}
