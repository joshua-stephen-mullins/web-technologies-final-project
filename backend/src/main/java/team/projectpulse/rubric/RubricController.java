package team.projectpulse.rubric;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Owner: Josh (Person 1)
// Related UCs: UC-1 (create rubric)
// Endpoints:
//   GET    /api/rubrics         - list all rubrics
//   POST   /api/rubrics         - UC-1: create rubric
//   GET    /api/rubrics/{id}    - get rubric details
//   PUT    /api/rubrics/{id}    - update rubric (creates a copy per UC-4 business rule)
//   DELETE /api/rubrics/{id}    - delete rubric
@RestController
@RequestMapping("/api/rubrics")
public class RubricController {
    // TODO: Implement
}
