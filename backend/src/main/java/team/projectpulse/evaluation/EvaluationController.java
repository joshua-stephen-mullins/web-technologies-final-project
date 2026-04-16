package team.projectpulse.evaluation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Owner: Whitney (Person 3)
// Endpoints:
//   POST /api/evaluations                               - UC-28: submit peer evaluation
//   GET  /api/evaluations/my-report?weekId=             - UC-29: student views own report
//   GET  /api/evaluations/report/section?sectionId=&weekId= - UC-31: section eval report
//   GET  /api/evaluations/report/student/{id}?start=&end=   - UC-33: student eval report over period
@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {
    // TODO: Implement
}
