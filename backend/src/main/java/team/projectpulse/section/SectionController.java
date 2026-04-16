package team.projectpulse.section;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.section.converter.SectionToSectionDtoConverter;
import team.projectpulse.section.converter.WeekToWeekDtoConverter;
import team.projectpulse.section.dto.SectionDto;
import team.projectpulse.section.dto.SectionRequest;
import team.projectpulse.section.dto.WeekDto;
import team.projectpulse.section.dto.WeekUpdateRequest;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;
    private final SectionToSectionDtoConverter sectionConverter;
    private final WeekToWeekDtoConverter weekConverter;

    public SectionController(SectionService sectionService,
                             SectionToSectionDtoConverter sectionConverter,
                             WeekToWeekDtoConverter weekConverter) {
        this.sectionService = sectionService;
        this.sectionConverter = sectionConverter;
        this.weekConverter = weekConverter;
    }

    // UC-2: Find/search sections
    @GetMapping
    public Result getSections(@RequestParam(required = false) String name) {
        List<SectionDto> dtos = sectionService.findSections(name).stream()
                .map(sectionConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Sections retrieved", dtos);
    }

    // UC-3: View section details
    @GetMapping("/{id}")
    public Result getSectionById(@PathVariable Integer id) {
        return new Result(true, StatusCode.SUCCESS, "Section retrieved",
                sectionConverter.convert(sectionService.findSectionById(id)));
    }

    // UC-4: Create section
    @PostMapping
    public Result createSection(@Valid @RequestBody SectionRequest request) {
        Section section = sectionService.createSection(request);
        return new Result(true, StatusCode.CREATED, "Section created", sectionConverter.convert(section));
    }

    // UC-5: Edit section
    @PutMapping("/{id}")
    public Result updateSection(@PathVariable Integer id, @Valid @RequestBody SectionRequest request) {
        Section section = sectionService.updateSection(id, request);
        return new Result(true, StatusCode.SUCCESS, "Section updated", sectionConverter.convert(section));
    }

    // UC-6: Get weeks for a section
    @GetMapping("/{id}/weeks")
    public Result getWeeks(@PathVariable Integer id) {
        List<WeekDto> dtos = sectionService.findWeeksBySectionId(id).stream()
                .map(weekConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Weeks retrieved", dtos);
    }

    // UC-6: Set active weeks
    @PutMapping("/{id}/weeks")
    public Result updateActiveWeeks(@PathVariable Integer id,
                                    @RequestBody WeekUpdateRequest request) {
        List<WeekDto> dtos = sectionService.setActiveWeeks(id, request).stream()
                .map(weekConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Active weeks updated", dtos);
    }
}
