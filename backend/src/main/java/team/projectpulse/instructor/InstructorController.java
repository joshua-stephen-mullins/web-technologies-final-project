package team.projectpulse.instructor;

import org.springframework.web.bind.annotation.*;
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

    // UC-18: Invite instructors via email
    @PostMapping("/invite")
    public Result inviteInstructors(@RequestBody List<String> emails) {
        int count = instructorService.inviteInstructors(emails);
        return new Result(true, StatusCode.CREATED, count + " invitation(s) sent");
    }
}