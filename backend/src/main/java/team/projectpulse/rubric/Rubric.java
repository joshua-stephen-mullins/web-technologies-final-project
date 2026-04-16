package team.projectpulse.rubric;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rubric")
public class Rubric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "rubric", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<Criterion> criteria = new ArrayList<>();

    public Rubric() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Criterion> getCriteria() { return criteria; }
    public void setCriteria(List<Criterion> criteria) { this.criteria = criteria; }

    public void addCriterion(Criterion criterion) {
        criteria.add(criterion);
        criterion.setRubric(this);
    }

    public void clearCriteria() {
        criteria.forEach(c -> c.setRubric(null));
        criteria.clear();
    }
}
