package team.projectpulse.team;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Owner: Oscar (Person 2)
// Endpoints:
//   GET    /api/teams                          - UC-7: find/search teams
//   POST   /api/teams                          - UC-9: create team
//   GET    /api/teams/{id}                     - UC-8: view team details
//   PUT    /api/teams/{id}                     - UC-10: edit team
//   DELETE /api/teams/{id}                     - UC-14: delete team
//   POST   /api/teams/{id}/students            - UC-12: assign students to team
//   DELETE /api/teams/{id}/students/{studentId}- UC-13: remove student from team
@RestController
@RequestMapping("/api/teams")
public class TeamController {
    // TODO: Implement
}
