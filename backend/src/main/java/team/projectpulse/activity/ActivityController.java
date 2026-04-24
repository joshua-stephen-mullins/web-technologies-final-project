package team.projectpulse.activity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import team.projectpulse.activity.converter.ActivityToActivityDtoConverter;
import team.projectpulse.activity.dto.ActivityDto;
import team.projectpulse.activity.dto.ActivityRequest;
import team.projectpulse.section.Week;
import team.projectpulse.section.converter.WeekToWeekDtoConverter;
import team.projectpulse.section.dto.WeekDto;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.List;

// Owner: Oscar (Person 2)
// Endpoints:
//   GET    /api/activities?weekId=                  - UC-27: get current student's WAR for a week
//   POST   /api/activities                          - UC-27: add activity to WAR
//   PUT    /api/activities/{id}                     - UC-27: edit activity
//   DELETE /api/activities/{id}                     - UC-27: delete activity
//   GET    /api/activities/report/team?teamId=&weekId= - UC-32: WAR report for a team
@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityToActivityDtoConverter activityConverter;
    private final WeekToWeekDtoConverter weekConverter;

    public ActivityController(ActivityService activityService,
                              ActivityToActivityDtoConverter activityConverter,
                              WeekToWeekDtoConverter weekConverter) {
        this.activityService = activityService;
        this.activityConverter = activityConverter;
        this.weekConverter = weekConverter;
    }

    // UC-27 helper: Returns the current student's active section weeks
    @GetMapping("/my-weeks")
    public Result getMyWeeks(Authentication auth) {
        List<WeekDto> dtos = activityService.getMyWeeks(auth.getName()).stream()
                .map(weekConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Weeks retrieved", dtos);
    }

    // UC-27: Get current student's activities for a week
    @GetMapping
    public Result getActivities(@RequestParam Integer weekId, Authentication auth) {
        List<ActivityDto> dtos = activityService.findStudentActivities(weekId, auth.getName()).stream()
                .map(activityConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Activities retrieved", dtos);
    }

    // UC-27: Add activity to WAR
    @PostMapping
    public Result createActivity(@RequestBody ActivityRequest request, Authentication auth) {
        ActivityDto dto = activityConverter.convert(activityService.createActivity(request, auth.getName()));
        return new Result(true, StatusCode.CREATED, "Activity created", dto);
    }

    // UC-27: Edit activity
    @PutMapping("/{id}")
    public Result updateActivity(@PathVariable Integer id, @RequestBody ActivityRequest request) {
        ActivityDto dto = activityConverter.convert(activityService.updateActivity(id, request));
        return new Result(true, StatusCode.SUCCESS, "Activity updated", dto);
    }

    // UC-27: Delete activity
    @DeleteMapping("/{id}")
    public Result deleteActivity(@PathVariable Integer id) {
        activityService.deleteActivity(id);
        return new Result(true, StatusCode.SUCCESS, "Activity deleted");
    }

    // UC-32: Team WAR report for a week
    @GetMapping("/report/team")
    public Result getTeamWARReport(@RequestParam Integer teamId, @RequestParam Integer weekId) {
        List<ActivityDto> dtos = activityService.generateTeamWARReport(teamId, weekId).stream()
                .map(activityConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "WAR report generated", dtos);
    }

    // UC-34: Student WAR report over a period (consumed by Whitney's StudentWARReport.vue)
    @GetMapping("/report/student/{studentId}")
    public Result getStudentWARReport(@PathVariable Integer studentId,
                                      @RequestParam List<Integer> weekIds) {
        List<ActivityDto> dtos = activityService.generateStudentWARReport(studentId, weekIds).stream()
                .map(activityConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Student WAR report generated", dtos);
    }
}
