package team.projectpulse.rubric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.projectpulse.system.exception.ObjectAlreadyExistsException;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RubricServiceTest {

    @Mock RubricRepository rubricRepository;

    @InjectMocks RubricService rubricService;

    private Rubric rubric1;
    private Rubric rubric2;

    @BeforeEach
    void setUp() {
        rubric1 = new Rubric();
        rubric1.setId(1);
        rubric1.setName("Peer Eval Rubric");

        Criterion c = new Criterion();
        c.setName("Communication");
        c.setMaxScore(BigDecimal.TEN);
        c.setSortOrder(0);
        rubric1.addCriterion(c);

        rubric2 = new Rubric();
        rubric2.setId(2);
        rubric2.setName("WAR Rubric");
    }

    // ── findAllRubrics ────────────────────────────────────────────────────────

    @Test
    void findAllRubrics_returnsAllRubrics() {
        given(rubricRepository.findAll()).willReturn(List.of(rubric1, rubric2));

        List<Rubric> result = rubricService.findAllRubrics();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Rubric::getName)
                .containsExactly("Peer Eval Rubric", "WAR Rubric");
    }

    @Test
    void findAllRubrics_returnsEmptyList_whenNoneExist() {
        given(rubricRepository.findAll()).willReturn(List.of());

        List<Rubric> result = rubricService.findAllRubrics();

        assertThat(result).isEmpty();
    }

    // ── findRubricById ────────────────────────────────────────────────────────

    @Test
    void findRubricById_returnsRubric_whenFound() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));

        Rubric result = rubricService.findRubricById(1);

        assertThat(result.getName()).isEqualTo("Peer Eval Rubric");
        assertThat(result.getCriteria()).hasSize(1);
    }

    @Test
    void findRubricById_throwsObjectNotFoundException_whenNotFound() {
        given(rubricRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> rubricService.findRubricById(99))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    // ── createRubric ──────────────────────────────────────────────────────────

    @Test
    void createRubric_savesAndReturnsRubric_withCriteria() {
        given(rubricRepository.existsByName("New Rubric")).willReturn(false);
        given(rubricRepository.save(any(Rubric.class))).willAnswer(inv -> {
            Rubric r = inv.getArgument(0);
            r.setId(10);
            return r;
        });

        List<RubricService.CriterionRequest> criteria = List.of(
                new RubricService.CriterionRequest("Teamwork", "Works well", BigDecimal.TEN),
                new RubricService.CriterionRequest("Quality",  "Good code",  new BigDecimal("8"))
        );

        Rubric result = rubricService.createRubric("New Rubric", criteria);

        assertThat(result.getName()).isEqualTo("New Rubric");
        assertThat(result.getCriteria()).hasSize(2);
        assertThat(result.getCriteria().get(0).getSortOrder()).isEqualTo(0);
        assertThat(result.getCriteria().get(1).getSortOrder()).isEqualTo(1);
        then(rubricRepository).should().save(any(Rubric.class));
    }

    @Test
    void createRubric_withNullCriteria_savesRubricWithNoCriteria() {
        given(rubricRepository.existsByName("Empty")).willReturn(false);
        given(rubricRepository.save(any(Rubric.class))).willAnswer(inv -> inv.getArgument(0));

        Rubric result = rubricService.createRubric("Empty", null);

        assertThat(result.getCriteria()).isEmpty();
    }

    @Test
    void createRubric_throwsObjectAlreadyExistsException_whenNameTaken() {
        given(rubricRepository.existsByName("Peer Eval Rubric")).willReturn(true);

        assertThatThrownBy(() -> rubricService.createRubric("Peer Eval Rubric", List.of()))
                .isInstanceOf(ObjectAlreadyExistsException.class)
                .hasMessageContaining("Peer Eval Rubric");
    }

    // ── updateRubric ──────────────────────────────────────────────────────────

    @Test
    void updateRubric_updatesNameAndReplacesAllCriteria() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));
        given(rubricRepository.existsByNameAndIdNot("Updated", 1)).willReturn(false);
        given(rubricRepository.save(any(Rubric.class))).willAnswer(inv -> inv.getArgument(0));

        List<RubricService.CriterionRequest> newCriteria = List.of(
                new RubricService.CriterionRequest("New Criterion", "", BigDecimal.TEN)
        );

        Rubric result = rubricService.updateRubric(1, "Updated", newCriteria);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getCriteria()).hasSize(1);
        assertThat(result.getCriteria().get(0).getName()).isEqualTo("New Criterion");
    }

    @Test
    void updateRubric_allowsSameName_whenNameUnchanged() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));
        given(rubricRepository.save(any(Rubric.class))).willAnswer(inv -> inv.getArgument(0));

        // Same name — existsByNameAndIdNot should NOT be called
        Rubric result = rubricService.updateRubric(1, "Peer Eval Rubric", List.of());

        assertThat(result.getName()).isEqualTo("Peer Eval Rubric");
        then(rubricRepository).should(never()).existsByNameAndIdNot(any(), any());
    }

    @Test
    void updateRubric_throwsObjectAlreadyExistsException_whenNewNameConflicts() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));
        given(rubricRepository.existsByNameAndIdNot("WAR Rubric", 1)).willReturn(true);

        assertThatThrownBy(() -> rubricService.updateRubric(1, "WAR Rubric", List.of()))
                .isInstanceOf(ObjectAlreadyExistsException.class);
    }

    // ── duplicateRubric ───────────────────────────────────────────────────────

    @Test
    void duplicateRubric_createsNewRubricWithCopySuffix() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));
        given(rubricRepository.existsByName("Peer Eval Rubric (Copy)")).willReturn(false);
        given(rubricRepository.save(any(Rubric.class))).willAnswer(inv -> inv.getArgument(0));

        Rubric copy = rubricService.duplicateRubric(1);

        assertThat(copy.getName()).isEqualTo("Peer Eval Rubric (Copy)");
        assertThat(copy.getCriteria()).hasSize(1);
        assertThat(copy.getCriteria().get(0).getName()).isEqualTo("Communication");
    }

    @Test
    void duplicateRubric_incrementsSuffix_whenCopyNameAlreadyTaken() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));
        given(rubricRepository.existsByName("Peer Eval Rubric (Copy)")).willReturn(true);
        given(rubricRepository.existsByName("Peer Eval Rubric (Copy 2)")).willReturn(false);
        given(rubricRepository.save(any(Rubric.class))).willAnswer(inv -> inv.getArgument(0));

        Rubric copy = rubricService.duplicateRubric(1);

        assertThat(copy.getName()).isEqualTo("Peer Eval Rubric (Copy 2)");
    }

    // ── deleteRubric ──────────────────────────────────────────────────────────

    @Test
    void deleteRubric_deletesSuccessfully_whenNotAssignedToSection() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));
        given(rubricRepository.countSectionsUsingRubric(1)).willReturn(0L);

        rubricService.deleteRubric(1);

        then(rubricRepository).should().delete(rubric1);
    }

    @Test
    void deleteRubric_throwsIllegalStateException_whenAssignedToSection() {
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric1));
        given(rubricRepository.countSectionsUsingRubric(1)).willReturn(2L);

        assertThatThrownBy(() -> rubricService.deleteRubric(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("assigned to one or more sections");
    }

    @Test
    void deleteRubric_throwsObjectNotFoundException_whenRubricDoesNotExist() {
        given(rubricRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> rubricService.deleteRubric(99))
                .isInstanceOf(ObjectNotFoundException.class);
    }
}
