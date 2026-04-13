package team.projectpulse.activity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Owner: Oscar (Person 2)
// Endpoints:
//   GET    /api/activities?studentId=&weekId=   - UC-27: get student's WAR for a week
//   POST   /api/activities                      - UC-27: add activity to WAR
//   PUT    /api/activities/{id}                 - UC-27: edit activity
//   DELETE /api/activities/{id}                 - UC-27: delete activity
//   GET    /api/activities/report/team          - UC-32: WAR report for a team
@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    // TODO: Implement
}
