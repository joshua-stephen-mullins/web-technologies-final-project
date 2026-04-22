package team.projectpulse.instructor;

import org.springframework.web.bind.annotation.*;
import team.projectpulse.instructor.dto.InstructorDetailDto;
import team.projectpulse.instructor.dto.InstructorDto;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.List;

// Owner: Whitney (Person 3)
@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    // UC-22: View details of a single instructor
    @GetMapping("/{id}")
    public Result findInstructor(@PathVariable Integer id) {
        InstructorDetailDto dto = instructorService.findInstructor(id);
        return new Result(true, StatusCode.SUCCESS, "Found instructor", dto);
    }

    // UC-23: Deactivate an instructor
    @PatchMapping("/{id}/deactivate")
    public Result deactivateInstructor(@PathVariable Integer id) {
        instructorService.deactivateInstructor(id);
        return new Result(true, StatusCode.SUCCESS, "Instructor deactivated");
    }

    // UC-18: Invite instructors via email
    @PostMapping("/invite")
    public Result inviteInstructors(@RequestBody List<String> emails) {
        int count = instructorService.inviteInstructors(emails);
        return new Result(true, StatusCode.CREATED, count + " invitation(s) sent");
    }

    // UC-21: Find instructors by optional search criteria
    @GetMapping
    public Result findInstructors(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String teamName,
            @RequestParam(required = false) String status
    ) {
        Boolean enabled = null;
        if ("active".equalsIgnoreCase(status)) enabled = true;
        else if ("deactivated".equalsIgnoreCase(status)) enabled = false;

        List<InstructorDto> dtos = instructorService.findInstructors(firstName, lastName, teamName, enabled);
        String message = dtos.isEmpty() ? "No matching instructors found" : "Found instructors";
        return new Result(true, StatusCode.SUCCESS, message, dtos);
    }
}
