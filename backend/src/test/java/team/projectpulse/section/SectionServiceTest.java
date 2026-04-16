package team.projectpulse.section;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team.projectpulse.rubric.Rubric;
import team.projectpulse.rubric.RubricRepository;
import team.projectpulse.section.dto.SectionRequest;
import team.projectpulse.section.dto.WeekUpdateRequest;
import team.projectpulse.system.exception.ObjectAlreadyExistsException;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock SectionRepository sectionRepository;
    @Mock WeekRepository weekRepository;
    @Mock RubricRepository rubricRepository;

    @InjectMocks SectionService sectionService;

    private Section section;
    private Rubric rubric;

    @BeforeEach
    void setUp() {
        rubric = new Rubric();
        rubric.setId(1);
        rubric.setName("Peer Eval Rubric");

        section = new Section();
        section.setId(1);
        section.setName("Spring 2026");
        section.setStartDate(LocalDate.of(2026, 1, 12));
        section.setEndDate(LocalDate.of(2026, 5, 2));
        section.setRubric(rubric);
    }

    // ── findSectionById ───────────────────────────────────────────────────────

    @Test
    void findSectionById_returnsSection_whenFound() {
        given(sectionRepository.findById(1)).willReturn(Optional.of(section));

        Section result = sectionService.findSectionById(1);

        assertThat(result.getName()).isEqualTo("Spring 2026");
    }

    @Test
    void findSectionById_throwsObjectNotFoundException_whenNotFound() {
        given(sectionRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.findSectionById(99))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    // ── findSections ──────────────────────────────────────────────────────────

    @Test
    void findSections_returnsAll_whenNoNameFilter() {
        given(sectionRepository.findAll()).willReturn(List.of(section));

        List<Section> result = sectionService.findSections(null);

        assertThat(result).hasSize(1);
    }

    @Test
    void findSections_returnsAll_whenBlankNameFilter() {
        given(sectionRepository.findAll()).willReturn(List.of(section));

        List<Section> result = sectionService.findSections("  ");

        assertThat(result).hasSize(1);
    }

    @Test
    void findSections_usesSpecification_whenNameFilterProvided() {
        given(sectionRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .willReturn(List.of(section));

        List<Section> result = sectionService.findSections("Spring");

        assertThat(result).hasSize(1);
        then(sectionRepository).should(never()).findAll();
    }

    // ── createSection ─────────────────────────────────────────────────────────

    @Test
    void createSection_savesSection_andGeneratesWeeks() {
        SectionRequest req = new SectionRequest("Fall 2026", "2026-08-24", "2026-12-12", null);
        given(sectionRepository.existsByName("Fall 2026")).willReturn(false);
        given(sectionRepository.save(any(Section.class))).willAnswer(inv -> {
            Section s = inv.getArgument(0);
            s.setId(10);
            return s;
        });

        Section result = sectionService.createSection(req);

        // 24 Aug → 12 Dec = 16 weeks
        assertThat(result.getWeeks()).hasSize(16);
        assertThat(result.getWeeks().get(0).getWeekNumber()).isEqualTo(1);
        assertThat(result.getWeeks().get(0).getStartDate()).isEqualTo(LocalDate.of(2026, 8, 24));
        assertThat(result.getWeeks().get(0).isActive()).isTrue();
    }

    @Test
    void createSection_assignsRubric_whenRubricIdProvided() {
        SectionRequest req = new SectionRequest("Fall 2026", "2026-08-24", "2026-12-12", 1);
        given(sectionRepository.existsByName("Fall 2026")).willReturn(false);
        given(rubricRepository.findById(1)).willReturn(Optional.of(rubric));
        given(sectionRepository.save(any(Section.class))).willAnswer(inv -> inv.getArgument(0));

        Section result = sectionService.createSection(req);

        assertThat(result.getRubric()).isNotNull();
        assertThat(result.getRubric().getName()).isEqualTo("Peer Eval Rubric");
    }

    @Test
    void createSection_leavesRubricNull_whenNoRubricIdProvided() {
        SectionRequest req = new SectionRequest("Fall 2026", "2026-08-24", "2026-12-12", null);
        given(sectionRepository.existsByName("Fall 2026")).willReturn(false);
        given(sectionRepository.save(any(Section.class))).willAnswer(inv -> inv.getArgument(0));

        Section result = sectionService.createSection(req);

        assertThat(result.getRubric()).isNull();
    }

    @Test
    void createSection_throwsObjectAlreadyExistsException_whenNameTaken() {
        SectionRequest req = new SectionRequest("Spring 2026", "2026-01-12", "2026-05-02", null);
        given(sectionRepository.existsByName("Spring 2026")).willReturn(true);

        assertThatThrownBy(() -> sectionService.createSection(req))
                .isInstanceOf(ObjectAlreadyExistsException.class)
                .hasMessageContaining("Spring 2026");
    }

    @Test
    void createSection_throwsObjectNotFoundException_whenRubricNotFound() {
        SectionRequest req = new SectionRequest("Fall 2026", "2026-08-24", "2026-12-12", 99);
        given(sectionRepository.existsByName("Fall 2026")).willReturn(false);
        given(rubricRepository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.createSection(req))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    // ── updateSection ─────────────────────────────────────────────────────────

    @Test
    void updateSection_updatesFieldsAndRegeneratesWeeks() {
        SectionRequest req = new SectionRequest("Spring 2026", "2026-01-12", "2026-04-25", null);
        given(sectionRepository.findById(1)).willReturn(Optional.of(section));
        given(sectionRepository.save(any(Section.class))).willAnswer(inv -> inv.getArgument(0));

        Section result = sectionService.updateSection(1, req);

        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2026, 4, 25));
        // Weeks should be regenerated from the new dates
        assertThat(result.getWeeks()).isNotEmpty();
    }

    @Test
    void updateSection_clearsRubric_whenRubricIdIsNull() {
        SectionRequest req = new SectionRequest("Spring 2026", "2026-01-12", "2026-05-02", null);
        given(sectionRepository.findById(1)).willReturn(Optional.of(section));
        given(sectionRepository.save(any(Section.class))).willAnswer(inv -> inv.getArgument(0));

        Section result = sectionService.updateSection(1, req);

        assertThat(result.getRubric()).isNull();
    }

    @Test
    void updateSection_throwsObjectAlreadyExistsException_whenNewNameConflicts() {
        SectionRequest req = new SectionRequest("Fall 2026", "2026-08-24", "2026-12-12", null);
        given(sectionRepository.findById(1)).willReturn(Optional.of(section));
        given(sectionRepository.existsByNameAndIdNot("Fall 2026", 1)).willReturn(true);

        assertThatThrownBy(() -> sectionService.updateSection(1, req))
                .isInstanceOf(ObjectAlreadyExistsException.class);
    }

    // ── setActiveWeeks ────────────────────────────────────────────────────────

    @Test
    void setActiveWeeks_marksSpecifiedWeeksInactive() {
        List<Week> weeks = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            Week w = new Week();
            w.setWeekNumber(i);
            w.setActive(true);
            weeks.add(w);
        }
        given(weekRepository.findBySectionIdOrderByWeekNumber(1)).willReturn(weeks);
        given(weekRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        WeekUpdateRequest req = new WeekUpdateRequest(List.of(2, 3));
        List<Week> result = sectionService.setActiveWeeks(1, req);

        assertThat(result.get(0).isActive()).isTrue();   // week 1 — active
        assertThat(result.get(1).isActive()).isFalse();  // week 2 — inactive
        assertThat(result.get(2).isActive()).isFalse();  // week 3 — inactive
        assertThat(result.get(3).isActive()).isTrue();   // week 4 — active
    }

    @Test
    void setActiveWeeks_allWeeksRemainActive_whenNoInactiveNumbersProvided() {
        List<Week> weeks = List.of(buildWeek(1), buildWeek(2));
        given(weekRepository.findBySectionIdOrderByWeekNumber(1)).willReturn(weeks);
        given(weekRepository.saveAll(anyList())).willAnswer(inv -> inv.getArgument(0));

        List<Week> result = sectionService.setActiveWeeks(1, new WeekUpdateRequest(null));

        assertThat(result).allMatch(Week::isActive);
    }

    // ── generateWeeks (via createSection) ────────────────────────────────────

    @Test
    void generateWeeks_eachWeekSpansSevenDays() {
        SectionRequest req = new SectionRequest("Test", "2026-09-07", "2026-09-27", null);
        given(sectionRepository.existsByName("Test")).willReturn(false);
        given(sectionRepository.save(any(Section.class))).willAnswer(inv -> inv.getArgument(0));

        Section result = sectionService.createSection(req);

        for (Week w : result.getWeeks()) {
            assertThat(w.getEndDate()).isEqualTo(w.getStartDate().plusDays(6));
        }
    }

    @Test
    void generateWeeks_weekNumbersAreSequential() {
        SectionRequest req = new SectionRequest("Test", "2026-09-07", "2026-09-27", null);
        given(sectionRepository.existsByName("Test")).willReturn(false);
        given(sectionRepository.save(any(Section.class))).willAnswer(inv -> inv.getArgument(0));

        Section result = sectionService.createSection(req);

        List<Week> weeks = result.getWeeks();
        for (int i = 0; i < weeks.size(); i++) {
            assertThat(weeks.get(i).getWeekNumber()).isEqualTo(i + 1);
        }
    }

    private Week buildWeek(int number) {
        Week w = new Week();
        w.setWeekNumber(number);
        w.setActive(true);
        return w;
    }
}
