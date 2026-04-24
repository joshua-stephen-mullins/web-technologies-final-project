package team.projectpulse.evaluation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.evaluation.dto.BatchEvaluationRequest;
import team.projectpulse.evaluation.dto.MyEvaluationReportDto;
import team.projectpulse.evaluation.dto.SubmitFormDto;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.List;

// Owner: Whitney (Person 3)
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    // UC-28: load form data — teammates, criteria, week, any existing submissions
    @GetMapping("/submit-form")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Result getSubmitForm(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        SubmitFormDto form = evaluationService.getSubmitForm(username);
        return new Result(true, StatusCode.SUCCESS, "Submit form loaded", form);
    }

    // UC-29: available completed weeks for the student's section
    @GetMapping("/my-report/weeks")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Result getMyReportWeeks(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();
        List<SubmitFormDto.WeekInfo> weeks = evaluationService.getMyReportWeeks(username);
        return new Result(true, StatusCode.SUCCESS, "Weeks loaded", weeks);
    }

    // UC-29: generate student's own peer evaluation report
    @GetMapping("/my-report")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Result getMyReport(@AuthenticationPrincipal Jwt jwt, @RequestParam Integer weekId) {
        String username = jwt.getSubject();
        MyEvaluationReportDto report = evaluationService.getMyReport(username, weekId);
        return new Result(true, StatusCode.SUCCESS, "Report generated", report);
    }

    // UC-28: upsert all evaluations for the week in one call
    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    public Result submitBatch(@AuthenticationPrincipal Jwt jwt,
                              @RequestBody BatchEvaluationRequest request) {
        String username = jwt.getSubject();
        evaluationService.submitEvaluations(username, request);
        return new Result(true, StatusCode.SUCCESS, "Evaluations submitted", null);
    }
}
