package team.projectpulse.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import team.projectpulse.system.exception.PasswordChangeIllegalArgumentException;
import team.projectpulse.user.dto.PasswordChangeRequest;
import team.projectpulse.user.dto.UserRegistrationRequest;
import team.projectpulse.user.dto.UserUpdateRequest;
import team.projectpulse.user.userinvitation.UserInvitation;
import team.projectpulse.user.userinvitation.UserInvitationService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserInvitationService invitationService;

    @InjectMocks UserService userService;

    private PeerEvaluationUser adminUser;

    @BeforeEach
    void setUp() {
        adminUser = new PeerEvaluationUser();
        adminUser.setId(1);
        adminUser.setUsername("admin@test.com");
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPassword("$2a$10$hashed");
        adminUser.setRoles("ROLE_ADMIN");
        adminUser.setEnabled(true);
    }

    // ── loadUserByUsername ────────────────────────────────────────────────────

    @Test
    void loadUserByUsername_returnsUserDetails_whenFound() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));

        UserDetails result = userService.loadUserByUsername("admin@test.com");

        assertThat(result.getUsername()).isEqualTo("admin@test.com");
    }

    @Test
    void loadUserByUsername_throwsUsernameNotFoundException_whenNotFound() {
        given(userRepository.findByUsername("nobody@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("nobody@test.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    // ── registerUser ──────────────────────────────────────────────────────────

    @Test
    void registerUser_createsAndSavesUser_withEncodedPassword() {
        UserInvitation invitation = new UserInvitation();
        invitation.setEmail("student@test.com");
        invitation.setRole("ROLE_STUDENT");

        UserRegistrationRequest req = new UserRegistrationRequest("Alice", null, "Smith", "password123");

        given(invitationService.validateToken("valid-token")).willReturn(invitation);
        given(userRepository.existsByUsername("student@test.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("$2a$encoded");
        given(userRepository.save(any(PeerEvaluationUser.class))).willAnswer(inv -> {
            PeerEvaluationUser u = inv.getArgument(0);
            u.setId(42);
            return u;
        });

        PeerEvaluationUser result = userService.registerUser("valid-token", req);

        assertThat(result.getUsername()).isEqualTo("student@test.com");
        assertThat(result.getFirstName()).isEqualTo("Alice");
        assertThat(result.getPassword()).isEqualTo("$2a$encoded");
        assertThat(result.getRoles()).isEqualTo("ROLE_STUDENT");
        assertThat(result.isEnabled()).isTrue();
        then(invitationService).should().markUsed(invitation);
    }

    @Test
    void registerUser_throwsIllegalStateException_whenEmailAlreadyExists() {
        UserInvitation invitation = new UserInvitation();
        invitation.setEmail("admin@test.com");
        invitation.setRole("ROLE_STUDENT");

        given(invitationService.validateToken("token")).willReturn(invitation);
        given(userRepository.existsByUsername("admin@test.com")).willReturn(true);

        assertThatThrownBy(() -> userService.registerUser("token",
                new UserRegistrationRequest("A", null, "B", "pw")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    // ── updateUser ────────────────────────────────────────────────────────────

    @Test
    void updateUser_updatesFirstAndLastName() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));
        given(userRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest("Joshua", "Mullins", null);
        PeerEvaluationUser result = userService.updateUser("admin@test.com", req);

        assertThat(result.getFirstName()).isEqualTo("Joshua");
        assertThat(result.getLastName()).isEqualTo("Mullins");
        assertThat(result.getUsername()).isEqualTo("admin@test.com"); // unchanged
    }

    @Test
    void updateUser_updatesEmail_whenNewEmailIsUnique() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));
        given(userRepository.existsByUsername("new@test.com")).willReturn(false);
        given(userRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest(null, null, "new@test.com");
        PeerEvaluationUser result = userService.updateUser("admin@test.com", req);

        assertThat(result.getUsername()).isEqualTo("new@test.com");
    }

    @Test
    void updateUser_doesNotChangeEmail_whenSameEmailSent() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));
        given(userRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest("Joshua", null, "admin@test.com");
        PeerEvaluationUser result = userService.updateUser("admin@test.com", req);

        assertThat(result.getUsername()).isEqualTo("admin@test.com");
        // existsByUsername should never be called for uniqueness check
        then(userRepository).should(never()).existsByUsername("admin@test.com");
    }

    @Test
    void updateUser_throwsIllegalStateException_whenNewEmailAlreadyInUse() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));
        given(userRepository.existsByUsername("taken@test.com")).willReturn(true);

        UserUpdateRequest req = new UserUpdateRequest(null, null, "taken@test.com");

        assertThatThrownBy(() -> userService.updateUser("admin@test.com", req))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already in use");
    }

    @Test
    void updateUser_ignoresBlankFields() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));
        given(userRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        UserUpdateRequest req = new UserUpdateRequest("", "  ", "");
        PeerEvaluationUser result = userService.updateUser("admin@test.com", req);

        // Blank fields should not overwrite existing values
        assertThat(result.getFirstName()).isEqualTo("Admin");
        assertThat(result.getLastName()).isEqualTo("User");
        assertThat(result.getUsername()).isEqualTo("admin@test.com");
    }

    // ── changePassword ────────────────────────────────────────────────────────

    @Test
    void changePassword_encodesAndSavesNewPassword() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));
        given(passwordEncoder.matches("oldpass", "$2a$10$hashed")).willReturn(true);
        given(passwordEncoder.encode("newpass")).willReturn("$2a$newHash");
        given(userRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        userService.changePassword("admin@test.com", new PasswordChangeRequest("oldpass", "newpass"));

        assertThat(adminUser.getPassword()).isEqualTo("$2a$newHash");
    }

    @Test
    void changePassword_throwsPasswordChangeIllegalArgumentException_whenOldPasswordWrong() {
        given(userRepository.findByUsername("admin@test.com")).willReturn(Optional.of(adminUser));
        given(passwordEncoder.matches("wrongpass", "$2a$10$hashed")).willReturn(false);

        assertThatThrownBy(() -> userService.changePassword("admin@test.com",
                new PasswordChangeRequest("wrongpass", "newpass")))
                .isInstanceOf(PasswordChangeIllegalArgumentException.class);
    }
}
