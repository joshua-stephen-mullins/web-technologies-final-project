package team.projectpulse.instructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Owner: Whitey (Person 3)
// Endpoints:
//   GET    /api/instructors                            - UC-21: find/search instructors
//   GET    /api/instructors/{id}                       - UC-22: view instructor details
//   PUT    /api/instructors/{id}/deactivate            - UC-23: deactivate instructor
//   PUT    /api/instructors/{id}/reactivate            - UC-24: reactivate instructor
//   POST   /api/teams/{id}/instructors                 - UC-19: assign instructors to team
//   DELETE /api/teams/{id}/instructors/{instructorId}  - UC-20: remove instructor from team
@RestController
@RequestMapping("/api/instructors")
public class InstructorController {
    // TODO: Implement
}
