package team.projectpulse.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.system.exception.PasswordChangeIllegalArgumentException;
import team.projectpulse.user.dto.PasswordChangeRequest;
import team.projectpulse.user.dto.UserRegistrationRequest;
import team.projectpulse.user.dto.UserUpdateRequest;
import team.projectpulse.user.userinvitation.UserInvitation;
import team.projectpulse.user.userinvitation.UserInvitationService;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInvitationService invitationService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       UserInvitationService invitationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.invitationService = invitationService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(MyUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public PeerEvaluationUser registerUser(String token, UserRegistrationRequest request) {
        UserInvitation invitation = invitationService.validateToken(token);

        if (userRepository.existsByUsername(invitation.getEmail())) {
            throw new IllegalStateException("An account with this email already exists");
        }

        PeerEvaluationUser user = new PeerEvaluationUser();
        user.setUsername(invitation.getEmail());
        user.setFirstName(request.firstName());
        user.setMiddleInitial(request.middleInitial() != null && !request.middleInitial().isBlank()
                ? request.middleInitial().toUpperCase()
                : null);
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(invitation.getRole());
        user.setEnabled(true);

        PeerEvaluationUser saved = userRepository.save(user);
        invitationService.markUsed(invitation);
        return saved;
    }

    public PeerEvaluationUser findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("user", username));
    }

    @Transactional
    public PeerEvaluationUser updateUser(String username, UserUpdateRequest request) {
        PeerEvaluationUser user = findByUsername(username);

        if (request.firstName() != null && !request.firstName().isBlank()) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null && !request.lastName().isBlank()) {
            user.setLastName(request.lastName());
        }
        if (request.email() != null && !request.email().isBlank()) {
            // Check uniqueness only if email is actually changing
            if (!request.email().equals(user.getUsername()) && userRepository.existsByUsername(request.email())) {
                throw new IllegalStateException("Email already in use");
            }
            user.setUsername(request.email());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(String username, PasswordChangeRequest request) {
        PeerEvaluationUser user = findByUsername(username);

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new PasswordChangeIllegalArgumentException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
