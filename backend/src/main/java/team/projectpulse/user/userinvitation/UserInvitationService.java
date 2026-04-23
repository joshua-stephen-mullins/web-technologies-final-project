package team.projectpulse.user.userinvitation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.UUID;

@Service
public class UserInvitationService {

    private static final Logger log = LoggerFactory.getLogger(UserInvitationService.class);

    private final UserInvitationRepository invitationRepository;
    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public UserInvitationService(UserInvitationRepository invitationRepository, JavaMailSender mailSender) {
        this.invitationRepository = invitationRepository;
        this.mailSender = mailSender;
    }

    public UserInvitation sendInvitation(String email, String role, Integer sectionId) {
        UserInvitation invitation = new UserInvitation();
        invitation.setEmail(email);
        invitation.setRole(role);
        invitation.setSectionId(sectionId);
        invitation.setInvitationToken(UUID.randomUUID().toString());

        invitationRepository.save(invitation);
        try {
            sendEmail(invitation);
        } catch (MailException e) {
            log.warn("Failed to send invitation email to {} — token saved, email skipped: {}", invitation.getEmail(), e.getMessage());
            log.info("Registration link: {}/register?token={}", frontendUrl, invitation.getInvitationToken());
        }
        return invitation;
    }

    public UserInvitation validateToken(String token) {
        UserInvitation invitation = invitationRepository.findByInvitationToken(token)
                .orElseThrow(() -> new ObjectNotFoundException("invitation", token));

        if (invitation.isUsed()) {
            throw new IllegalStateException("Invitation token has already been used");
        }

        return invitation;
    }

    public void markUsed(UserInvitation invitation) {
        invitation.setUsed(true);
        invitationRepository.save(invitation);
    }

    private void sendEmail(UserInvitation invitation) {
        String registerUrl = frontendUrl + "/register?token=" + invitation.getInvitationToken();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(invitation.getEmail());
        message.setSubject("You've been invited to Project Pulse");
        message.setText("You have been invited to join Project Pulse as " + invitation.getRole() + ".\n\n"
                + "Click the link below to set up your account:\n" + registerUrl + "\n\n"
                + "This invitation is single-use.");
        mailSender.send(message);
    }
}
