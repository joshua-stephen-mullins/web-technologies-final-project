package team.projectpulse.user.resetpassword;

import org.springframework.data.jpa.repository.JpaRepository;

// Owner: Josh (Person 1)
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    // TODO: findByToken(String token): Optional<PasswordResetToken>
}
