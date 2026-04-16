package team.projectpulse.evaluation;

import jakarta.persistence.*;
import team.projectpulse.rubric.Criterion;

import java.math.BigDecimal;

// Owner: Whitney (Person 3)
// A single criterion score within a peer evaluation (e.g. Quality of Work: 8/10)
@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peer_evaluation_id", nullable = false)
    private PeerEvaluation peerEvaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterion_id", nullable = false)
    private Criterion criterion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    public Rating() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public PeerEvaluation getPeerEvaluation() { return peerEvaluation; }
    public void setPeerEvaluation(PeerEvaluation peerEvaluation) { this.peerEvaluation = peerEvaluation; }

    public Criterion getCriterion() { return criterion; }
    public void setCriterion(Criterion criterion) { this.criterion = criterion; }

    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
}
