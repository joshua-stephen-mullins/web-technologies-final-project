package team.projectpulse.user;

import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;
import team.projectpulse.user.converter.UserToUserDtoConverter;
import team.projectpulse.user.dto.ForgotPasswordRequest;
import team.projectpulse.user.dto.PasswordChangeRequest;
import team.projectpulse.user.dto.ResetPasswordRequest;
import team.projectpulse.user.dto.UserRegistrationRequest;
import team.projectpulse.user.dto.UserUpdateRequest;
import team.projectpulse.user.resetpassword.PasswordResetService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserToUserDtoConverter userConverter;
    private final PasswordResetService passwordResetService;

    public UserController(UserService userService,
                          UserToUserDtoConverter userConverter,
                          PasswordResetService passwordResetService) {
        this.userService = userService;
        this.userConverter = userConverter;
        this.passwordResetService = passwordResetService;
    }

    // UC-25 / UC-30: Complete registration via invite token
    @PostMapping("/register")
    public Result register(@RequestParam String token,
                           @Valid @RequestBody UserRegistrationRequest request) {
        PeerEvaluationUser user = userService.registerUser(token, request);
        return new Result(true, StatusCode.CREATED, "Account created successfully", userConverter.convert(user));
    }

    // UC-26: Get current user profile
    @GetMapping("/me")
    public Result getMe(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        PeerEvaluationUser user = userService.findByUsername(username);
        return new Result(true, StatusCode.SUCCESS, "User profile retrieved", userConverter.convert(user));
    }

    // UC-26: Update current user profile
    @PutMapping("/me")
    public Result updateMe(@AuthenticationPrincipal Jwt jwt,
                           @Valid @RequestBody UserUpdateRequest request) {
        String username = jwt.getSubject();
        PeerEvaluationUser updated = userService.updateUser(username, request);
        return new Result(true, StatusCode.SUCCESS, "Profile updated", userConverter.convert(updated));
    }

    // Change password (authenticated)
    @PutMapping("/me/password")
    public Result changePassword(@AuthenticationPrincipal Jwt jwt,
                                 @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(jwt.getSubject(), request);
        return new Result(true, StatusCode.SUCCESS, "Password changed successfully");
    }

    // Forgot password — sends reset email
    @PostMapping("/forgot-password")
    public Result forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.initiateReset(request.email());
        // Always return success to prevent email enumeration
        return new Result(true, StatusCode.SUCCESS, "If that email is registered, a reset link has been sent");
    }

    // Complete password reset via token
    @PostMapping("/reset-password")
    public Result resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.completeReset(request.token(), request.newPassword());
        return new Result(true, StatusCode.SUCCESS, "Password reset successfully");
    }
}
