package team.projectpulse.rubric;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.rubric.converter.RubricToRubricDtoConverter;
import team.projectpulse.rubric.dto.RubricDto;
import team.projectpulse.rubric.dto.RubricRequest;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.List;

@RestController
@RequestMapping("/api/rubrics")
public class RubricController {

    private final RubricService rubricService;
    private final RubricToRubricDtoConverter converter;

    public RubricController(RubricService rubricService, RubricToRubricDtoConverter converter) {
        this.rubricService = rubricService;
        this.converter = converter;
    }

    @GetMapping
    public Result getAllRubrics() {
        List<RubricDto> dtos = rubricService.findAllRubrics().stream()
                .map(converter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Rubrics retrieved", dtos);
    }

    @GetMapping("/{id}")
    public Result getRubricById(@PathVariable Integer id) {
        return new Result(true, StatusCode.SUCCESS, "Rubric retrieved", converter.convert(rubricService.findRubricById(id)));
    }

    @PostMapping
    public Result createRubric(@Valid @RequestBody RubricRequest request) {
        List<RubricService.CriterionRequest> criteriaReqs = toCriterionRequests(request);
        Rubric rubric = rubricService.createRubric(request.name(), criteriaReqs);
        return new Result(true, StatusCode.CREATED, "Rubric created", converter.convert(rubric));
    }

    @PutMapping("/{id}")
    public Result updateRubric(@PathVariable Integer id, @Valid @RequestBody RubricRequest request) {
        List<RubricService.CriterionRequest> criteriaReqs = toCriterionRequests(request);
        Rubric rubric = rubricService.updateRubric(id, request.name(), criteriaReqs);
        return new Result(true, StatusCode.SUCCESS, "Rubric updated", converter.convert(rubric));
    }

    @DeleteMapping("/{id}")
    public Result deleteRubric(@PathVariable Integer id) {
        rubricService.deleteRubric(id);
        return new Result(true, StatusCode.SUCCESS, "Rubric deleted");
    }

    private List<RubricService.CriterionRequest> toCriterionRequests(RubricRequest request) {
        if (request.criteria() == null) return List.of();
        return request.criteria().stream()
                .map(c -> new RubricService.CriterionRequest(c.name(), c.description(), c.maxScore()))
                .toList();
    }
}
