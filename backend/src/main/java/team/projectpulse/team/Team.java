package team.projectpulse.team;

import jakarta.persistence.*;
import team.projectpulse.section.Section;
import team.projectpulse.user.PeerEvaluationUser;

import java.util.ArrayList;
import java.util.List;

// Owner: Oscar (Person 2)
// Related UCs: UC-7,8,9,10,13,14
@Entity
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @OneToMany
    @JoinColumn(name = "team_id")
    private List<PeerEvaluationUser> students = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "team_instructor",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private List<PeerEvaluationUser> instructors = new ArrayList<>();

    public Team() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }

    public List<PeerEvaluationUser> getStudents() { return students; }
    public void setStudents(List<PeerEvaluationUser> students) { this.students = students; }

    public List<PeerEvaluationUser> getInstructors() { return instructors; }
    public void setInstructors(List<PeerEvaluationUser> instructors) { this.instructors = instructors; }
}
