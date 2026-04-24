package team.projectpulse.student;

import org.springframework.web.bind.annotation.*;
import team.projectpulse.student.dto.StudentDto;
import team.projectpulse.student.dto.StudentInviteRequest;
import team.projectpulse.system.Result;
import team.projectpulse.system.StatusCode;

import java.util.List;

// Owner: Oscar (Person 2)
// Endpoints:
//   GET    /api/students              - UC-15: find/search students
//   GET    /api/students/{id}         - UC-16: view student details
//   DELETE /api/students/{id}         - UC-17: delete student
//   POST   /api/students/invite       - UC-11: invite students via email
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // UC-15: Find/search students
    @GetMapping
    public Result findStudents(@RequestParam(required = false) String firstName,
                               @RequestParam(required = false) String lastName,
                               @RequestParam(required = false) String email,
                               @RequestParam(required = false) Integer teamId) {
        List<StudentDto> dtos = studentService.findStudents(firstName, lastName, email, teamId);
        return new Result(true, StatusCode.SUCCESS, "Found students", dtos);
    }

    // UC-16: View student details
    @GetMapping("/{id}")
    public Result findStudentById(@PathVariable Integer id) {
        StudentDto dto = studentService.findStudentById(id);
        return new Result(true, StatusCode.SUCCESS, "Found student", dto);
    }

    // UC-17: Delete student
    @DeleteMapping("/{id}")
    public Result deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return new Result(true, StatusCode.SUCCESS, "Student deleted");
    }

    // UC-11: Invite students via email
    @PostMapping("/invite")
    public Result inviteStudents(@RequestBody StudentInviteRequest request) {
        int count = studentService.inviteStudents(request.emails(), request.sectionId());
        return new Result(true, StatusCode.CREATED, count + " invitation(s) sent");
    }
}
