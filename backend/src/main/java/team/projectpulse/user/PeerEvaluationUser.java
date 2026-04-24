package team.projectpulse.user;

import jakarta.persistence.*;

@Entity
@Table(name = "peer_evaluation_user")
public class PeerEvaluationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_initial")
    private String middleInitial;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // Space-separated roles e.g. "ROLE_ADMIN" or "ROLE_STUDENT"
    @Column(nullable = false)
    private String roles;

    @Column(nullable = false)
    private boolean enabled = true;

    // Read-only — Team.students @OneToMany @JoinColumn owns writes to this FK
    @Column(name = "team_id", insertable = false, updatable = false)
    private Integer teamId;

    public PeerEvaluationUser() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleInitial() { return middleInitial; }
    public void setMiddleInitial(String middleInitial) { this.middleInitial = middleInitial; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
}
