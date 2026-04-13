package team.projectpulse.section;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Owner: Josh (Person 1)
// Related UCs: UC-2 (find), UC-3 (view), UC-4 (create), UC-5 (edit), UC-6 (active weeks)
// Endpoints:
//   GET    /api/sections                       - UC-2: find/search sections
//   POST   /api/sections                       - UC-4: create section
//   GET    /api/sections/{id}                  - UC-3: view section details
//   PUT    /api/sections/{id}                  - UC-5: edit section
//   PUT    /api/sections/{id}/weeks            - UC-6: set active weeks
@RestController
@RequestMapping("/api/sections")
public class SectionController {
    // TODO: Implement
}
