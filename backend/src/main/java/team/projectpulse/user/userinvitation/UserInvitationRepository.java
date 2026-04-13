package team.projectpulse.user.userinvitation;

import org.springframework.data.jpa.repository.JpaRepository;

// Owner: Josh (Person 1)
public interface UserInvitationRepository extends JpaRepository<UserInvitation, Integer> {
    // TODO: findByInvitationToken(String token): Optional<UserInvitation>
}
