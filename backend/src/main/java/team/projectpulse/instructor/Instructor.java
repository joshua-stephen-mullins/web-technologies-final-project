package team.projectpulse.instructor;

import jakarta.persistence.*;
import team.projectpulse.team.Team;

import java.util.ArrayList;
import java.util.List;

// Owner: Whitney (Person 3)
// Related UCs: UC-18,19,20,21,22,23,24,30
// Instructors are PeerEvaluationUsers with ROLE_INSTRUCTOR — mapped to the same table for role-filtered queries
@Entity
@Table(name = "peer_evaluation_user")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String roles;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "team_id", insertable = false, updatable = false)
    private Integer teamId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "team_instructor",
            joinColumns = @JoinColumn(name = "instructor_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();

    public Instructor() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }

    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }
}
