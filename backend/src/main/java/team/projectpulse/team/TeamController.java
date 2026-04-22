package team.projectpulse.team;

import org.springframework.web.bind.annotation.*;
import team.projectpulse.instructor.InstructorService;
import team.projectpulse.instructor.dto.InstructorDto;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;
import team.projectpulse.team.converter.TeamToTeamDtoConverter;
import team.projectpulse.team.dto.TeamDto;
import team.projectpulse.user.PeerEvaluationUser;

import java.util.List;

// Owner: Oscar (Person 2) for team CRUD; Whitney (Person 3) for UC-19/20 instructor endpoints
// Endpoints:
//   GET    /api/teams                              - UC-7: find/search teams
//   GET    /api/teams/{id}                         - UC-8: view team details
//   POST   /api/teams                              - UC-9: create team (TODO Oscar)
//   PUT    /api/teams/{id}                         - UC-10: edit team (TODO Oscar)
//   DELETE /api/teams/{id}                         - UC-14: delete team (TODO Oscar)
//   POST   /api/teams/{id}/students                - UC-12: assign students (TODO Oscar)
//   DELETE /api/teams/{id}/students/{studentId}    - UC-13: remove student (TODO Oscar)
//   GET    /api/teams/{id}/instructors             - UC-19: view team instructors
//   POST   /api/teams/{id}/instructors             - UC-19: assign instructors to team
//   DELETE /api/teams/{id}/instructors/{iid}       - UC-20: remove instructor from team
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final InstructorService instructorService;
    private final TeamToTeamDtoConverter teamConverter;

    public TeamController(TeamService teamService,
                          InstructorService instructorService,
                          TeamToTeamDtoConverter teamConverter) {
        this.teamService = teamService;
        this.instructorService = instructorService;
        this.teamConverter = teamConverter;
    }

    // UC-7: List all teams
    @GetMapping
    public Result findAllTeams() {
        List<TeamDto> dtos = teamService.findAllTeams().stream()
                .map(teamConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Found all teams", dtos);
    }

    // UC-8: Get team by id
    @GetMapping("/{id}")
    public Result findTeamById(@PathVariable Integer id) {
        TeamDto dto = teamConverter.convert(teamService.findTeamById(id));
        return new Result(true, StatusCode.SUCCESS, "Found team", dto);
    }

    // UC-19: Get instructors currently assigned to a team
    @GetMapping("/{id}/instructors")
    public Result findTeamInstructors(@PathVariable Integer id) {
        List<InstructorDto> dtos = toInstructorDtos(instructorService.findTeamInstructors(id));
        return new Result(true, StatusCode.SUCCESS, "Found team instructors", dtos);
    }

    // UC-19: Assign instructors to a team
    @PostMapping("/{id}/instructors")
    public Result assignInstructors(@PathVariable Integer id, @RequestBody List<Integer> instructorIds) {
        List<InstructorDto> dtos = toInstructorDtos(instructorService.assignInstructorsToTeam(id, instructorIds));
        return new Result(true, StatusCode.SUCCESS, "Instructors assigned", dtos);
    }

    // UC-20: Remove an instructor from a team
    @DeleteMapping("/{id}/instructors/{instructorId}")
    public Result removeInstructor(@PathVariable Integer id, @PathVariable Integer instructorId) {
        instructorService.removeInstructorFromTeam(id, instructorId);
        return new Result(true, StatusCode.SUCCESS, "Instructor removed from team");
    }

    private List<InstructorDto> toInstructorDtos(List<PeerEvaluationUser> users) {
        return users.stream()
                .map(u -> new InstructorDto(u.getId(), u.getFirstName(), u.getLastName(), u.getUsername(), u.isEnabled(), List.of()))
                .toList();
    }
}
