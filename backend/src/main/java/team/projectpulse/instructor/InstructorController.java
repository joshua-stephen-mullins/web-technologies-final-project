package team.projectpulse.instructor;

import org.springframework.web.bind.annotation.*;
import team.projectpulse.instructor.converter.InstructorToInstructorDtoConverter;
import team.projectpulse.instructor.dto.InstructorDto;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.List;

// Owner: Whitney (Person 3)
@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final InstructorService instructorService;
    private final InstructorToInstructorDtoConverter instructorConverter;

    public InstructorController(InstructorService instructorService,
                                InstructorToInstructorDtoConverter instructorConverter) {
        this.instructorService = instructorService;
        this.instructorConverter = instructorConverter;
    }

    // UC-18: Invite instructors via email
    @PostMapping("/invite")
    public Result inviteInstructors(@RequestBody List<String> emails) {
        int count = instructorService.inviteInstructors(emails);
        return new Result(true, StatusCode.CREATED, count + " invitation(s) sent");
    }

    // UC-21: List all instructors
    @GetMapping
    public Result findAllInstructors() {
        List<InstructorDto> dtos = instructorService.findAllInstructors().stream()
                .map(instructorConverter::convert)
                .toList();
        return new Result(true, StatusCode.SUCCESS, "Found all instructors", dtos);
    }
}
