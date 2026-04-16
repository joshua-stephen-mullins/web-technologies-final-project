package team.projectpulse.evaluation;

import jakarta.persistence.*;
import team.projectpulse.section.Week;
import team.projectpulse.user.PeerEvaluationUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Owner: Whitney (Person 3)
// One student's evaluation of one teammate for one week
// Related UCs: UC-28 (submit), UC-29 (student view), UC-31 (section report), UC-33 (student report)
@Entity
@Table(name = "peer_evaluation",
       uniqueConstraints = @UniqueConstraint(columnNames = {"evaluator_id", "evaluatee_id", "week_id"}))
public class PeerEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluator_id", nullable = false)
    private PeerEvaluationUser evaluator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluatee_id", nullable = false)
    private PeerEvaluationUser evaluatee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "week_id", nullable = false)
    private Week week;

    @Column(name = "public_comments", columnDefinition = "TEXT")
    private String publicComments;

    @Column(name = "private_comments", columnDefinition = "TEXT")
    private String privateComments;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "peerEvaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }

    public PeerEvaluation() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public PeerEvaluationUser getEvaluator() { return evaluator; }
    public void setEvaluator(PeerEvaluationUser evaluator) { this.evaluator = evaluator; }

    public PeerEvaluationUser getEvaluatee() { return evaluatee; }
    public void setEvaluatee(PeerEvaluationUser evaluatee) { this.evaluatee = evaluatee; }

    public Week getWeek() { return week; }
    public void setWeek(Week week) { this.week = week; }

    public String getPublicComments() { return publicComments; }
    public void setPublicComments(String publicComments) { this.publicComments = publicComments; }

    public String getPrivateComments() { return privateComments; }
    public void setPrivateComments(String privateComments) { this.privateComments = privateComments; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }

    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }
}
