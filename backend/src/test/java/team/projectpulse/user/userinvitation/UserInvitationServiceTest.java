package team.projectpulse.user.userinvitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserInvitationServiceTest {

    @Mock UserInvitationRepository invitationRepository;
    @Mock JavaMailSender mailSender;

    @InjectMocks UserInvitationService userInvitationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userInvitationService, "frontendUrl", "http://localhost:5173");
    }

    // ── sendInvitation ────────────────────────────────────────────────────────

    @Test
    void sendInvitation_savesInvitationAndSendsEmail() {
        given(invitationRepository.save(any(UserInvitation.class))).willAnswer(inv -> inv.getArgument(0));

        UserInvitation result = userInvitationService.sendInvitation("student@test.com", "ROLE_STUDENT", 1);

        assertThat(result.getEmail()).isEqualTo("student@test.com");
        assertThat(result.getRole()).isEqualTo("ROLE_STUDENT");
        assertThat(result.getSectionId()).isEqualTo(1);
        assertThat(result.getInvitationToken()).isNotBlank();

        then(invitationRepository).should().save(any(UserInvitation.class));
        then(mailSender).should().send(any(SimpleMailMessage.class));
    }

    @Test
    void sendInvitation_emailContainsRegisterLinkWithToken() {
        given(invitationRepository.save(any(UserInvitation.class))).willAnswer(inv -> inv.getArgument(0));

        ArgumentCaptor<SimpleMailMessage> emailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        userInvitationService.sendInvitation("student@test.com", "ROLE_STUDENT", null);

        then(mailSender).should().send(emailCaptor.capture());
        SimpleMailMessage sentEmail = emailCaptor.getValue();

        assertThat(sentEmail.getTo()).contains("student@test.com");
        assertThat(sentEmail.getSubject()).contains("Project Pulse");
        assertThat(sentEmail.getText()).contains("http://localhost:5173/register?token=");
    }

    @Test
    void sendInvitation_eachTokenIsUnique() {
        given(invitationRepository.save(any(UserInvitation.class))).willAnswer(inv -> inv.getArgument(0));

        UserInvitation inv1 = userInvitationService.sendInvitation("a@test.com", "ROLE_STUDENT", null);
        UserInvitation inv2 = userInvitationService.sendInvitation("b@test.com", "ROLE_STUDENT", null);

        assertThat(inv1.getInvitationToken()).isNotEqualTo(inv2.getInvitationToken());
    }

    // ── validateToken ─────────────────────────────────────────────────────────

    @Test
    void validateToken_returnsInvitation_whenTokenIsValidAndUnused() {
        UserInvitation invitation = new UserInvitation();
        invitation.setInvitationToken("valid-token");
        invitation.setEmail("student@test.com");
        invitation.setUsed(false);

        given(invitationRepository.findByInvitationToken("valid-token")).willReturn(Optional.of(invitation));

        UserInvitation result = userInvitationService.validateToken("valid-token");

        assertThat(result.getEmail()).isEqualTo("student@test.com");
    }

    @Test
    void validateToken_throwsObjectNotFoundException_whenTokenDoesNotExist() {
        given(invitationRepository.findByInvitationToken("bad-token")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userInvitationService.validateToken("bad-token"))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void validateToken_throwsIllegalStateException_whenTokenAlreadyUsed() {
        UserInvitation invitation = new UserInvitation();
        invitation.setInvitationToken("used-token");
        invitation.setUsed(true);

        given(invitationRepository.findByInvitationToken("used-token")).willReturn(Optional.of(invitation));

        assertThatThrownBy(() -> userInvitationService.validateToken("used-token"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been used");
    }

    // ── markUsed ──────────────────────────────────────────────────────────────

    @Test
    void markUsed_setsUsedFlagAndSaves() {
        UserInvitation invitation = new UserInvitation();
        invitation.setUsed(false);
        given(invitationRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

        userInvitationService.markUsed(invitation);

        assertThat(invitation.isUsed()).isTrue();
        then(invitationRepository).should().save(invitation);
    }
}
