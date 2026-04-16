package team.projectpulse.section;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.projectpulse.rubric.Rubric;
import team.projectpulse.rubric.RubricRepository;
import team.projectpulse.section.dto.SectionRequest;
import team.projectpulse.section.dto.WeekUpdateRequest;
import team.projectpulse.system.exception.ObjectAlreadyExistsException;
import team.projectpulse.system.exception.ObjectNotFoundException;

import java.time.LocalDate;
import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;
    private final WeekRepository weekRepository;
    private final RubricRepository rubricRepository;

    public SectionService(SectionRepository sectionRepository,
                          WeekRepository weekRepository,
                          RubricRepository rubricRepository) {
        this.sectionRepository = sectionRepository;
        this.weekRepository = weekRepository;
        this.rubricRepository = rubricRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public List<Section> findSections(String name) {
        if (name != null && !name.isBlank()) {
            Specification<Section> spec = SectionSpecs.hasSectionName(name);
            return sectionRepository.findAll(spec);
        }
        return sectionRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public Section findSectionById(Integer id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("section", id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Section createSection(SectionRequest request) {
        if (sectionRepository.existsByName(request.name())) {
            throw new ObjectAlreadyExistsException("A section named '" + request.name() + "' already exists");
        }

        Section section = new Section();
        section.setName(request.name());
        section.setStartDate(LocalDate.parse(request.startDate()));
        section.setEndDate(LocalDate.parse(request.endDate()));

        if (request.rubricId() != null) {
            Rubric rubric = rubricRepository.findById(request.rubricId())
                    .orElseThrow(() -> new ObjectNotFoundException("rubric", request.rubricId()));
            section.setRubric(rubric);
        }

        Section saved = sectionRepository.save(section);
        generateWeeks(saved);
        return sectionRepository.save(saved);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public Section updateSection(Integer id, SectionRequest request) {
        Section section = findSectionById(id);

        if (!section.getName().equals(request.name()) && sectionRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ObjectAlreadyExistsException("A section named '" + request.name() + "' already exists");
        }

        section.setName(request.name());
        section.setStartDate(LocalDate.parse(request.startDate()));
        section.setEndDate(LocalDate.parse(request.endDate()));

        if (request.rubricId() != null) {
            Rubric rubric = rubricRepository.findById(request.rubricId())
                    .orElseThrow(() -> new ObjectNotFoundException("rubric", request.rubricId()));
            section.setRubric(rubric);
        } else {
            section.setRubric(null);
        }

        // Regenerate weeks if dates changed
        section.clearWeeks();
        generateWeeks(section);

        return sectionRepository.save(section);
    }

    public List<Week> findWeeksBySectionId(Integer sectionId) {
        findSectionById(sectionId); // validate section exists
        return weekRepository.findBySectionIdOrderByWeekNumber(sectionId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public List<Week> setActiveWeeks(Integer sectionId, WeekUpdateRequest request) {
        List<Week> weeks = weekRepository.findBySectionIdOrderByWeekNumber(sectionId);
        List<Integer> inactiveNums = request.inactiveWeekNumbers() != null ? request.inactiveWeekNumbers() : List.of();

        for (Week week : weeks) {
            week.setActive(!inactiveNums.contains(week.getWeekNumber()));
        }

        weekRepository.saveAll(weeks);
        return weeks;
    }

    private void generateWeeks(Section section) {
        LocalDate current = section.getStartDate();
        int weekNumber = 1;

        while (!current.isAfter(section.getEndDate())) {
            Week week = new Week();
            week.setWeekNumber(weekNumber++);
            week.setStartDate(current);
            week.setEndDate(current.plusDays(6));
            week.setActive(true);
            section.addWeek(week);
            current = current.plusWeeks(1);
        }
    }
}
