package team.projectpulse.user.resetpassword;

import jakarta.persistence.*;
import team.projectpulse.user.PeerEvaluationUser;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private PeerEvaluationUser user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiry;

    public PasswordResetToken() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public PeerEvaluationUser getUser() { return user; }
    public void setUser(PeerEvaluationUser user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiry() { return expiry; }
    public void setExpiry(LocalDateTime expiry) { this.expiry = expiry; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }
}
