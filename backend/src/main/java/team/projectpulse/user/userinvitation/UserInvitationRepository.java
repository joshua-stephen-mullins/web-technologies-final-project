package team.projectpulse.user.userinvitation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInvitationRepository extends JpaRepository<UserInvitation, Integer> {
    Optional<UserInvitation> findByInvitationToken(String token);
    Optional<UserInvitation> findByEmailAndUsedFalse(String email);
}
