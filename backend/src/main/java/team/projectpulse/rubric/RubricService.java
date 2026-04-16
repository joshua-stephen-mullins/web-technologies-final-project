package team.projectpulse.rubric;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.system.exception.ObjectAlreadyExistsException;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.util.List;

@Service
public class RubricService {

    private final RubricRepository rubricRepository;

    public RubricService(RubricRepository rubricRepository) {
        this.rubricRepository = rubricRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<Rubric> findAllRubrics() {
        return rubricRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public Rubric findRubricById(Integer id) {
        return rubricRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("rubric", id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Rubric createRubric(String name, List<CriterionRequest> criteriaRequests) {
        if (rubricRepository.existsByName(name)) {
            throw new ObjectAlreadyExistsException("A rubric named '" + name + "' already exists");
        }

        Rubric rubric = new Rubric();
        rubric.setName(name);
        applyCriteria(rubric, criteriaRequests);
        return rubricRepository.save(rubric);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Rubric updateRubric(Integer id, String name, List<CriterionRequest> criteriaRequests) {
        Rubric rubric = findRubricById(id);

        if (!rubric.getName().equals(name) && rubricRepository.existsByNameAndIdNot(name, id)) {
            throw new ObjectAlreadyExistsException("A rubric named '" + name + "' already exists");
        }

        rubric.setName(name);
        rubric.clearCriteria();
        applyCriteria(rubric, criteriaRequests);
        return rubricRepository.save(rubric);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Rubric duplicateRubric(Integer id) {
        Rubric original = findRubricById(id);

        String copyName = original.getName() + " (Copy)";
        int suffix = 1;
        while (rubricRepository.existsByName(copyName)) {
            copyName = original.getName() + " (Copy " + (++suffix) + ")";
        }

        Rubric copy = new Rubric();
        copy.setName(copyName);
        original.getCriteria().forEach(c -> {
            Criterion newC = new Criterion();
            newC.setName(c.getName());
            newC.setDescription(c.getDescription());
            newC.setMaxScore(c.getMaxScore());
            newC.setSortOrder(c.getSortOrder());
            copy.addCriterion(newC);
        });

        return rubricRepository.save(copy);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public void deleteRubric(Integer id) {
        Rubric rubric = findRubricById(id);
        if (rubricRepository.countSectionsUsingRubric(id) > 0) {
            throw new IllegalStateException("Cannot delete rubric — it is assigned to one or more sections");
        }
        rubricRepository.delete(rubric);
    }

    private void applyCriteria(Rubric rubric, List<CriterionRequest> requests) {
        if (requests == null) return;
        for (int i = 0; i < requests.size(); i++) {
            CriterionRequest req = requests.get(i);
            Criterion criterion = new Criterion();
            criterion.setName(req.name());
            criterion.setDescription(req.description() != null ? req.description() : "");
            criterion.setMaxScore(req.maxScore());
            criterion.setSortOrder(i);
            rubric.addCriterion(criterion);
        }
    }

    // Inner record for criterion input — avoids coupling to the DTO layer
    public record CriterionRequest(String name, String description, java.math.BigDecimal maxScore) {}
}
