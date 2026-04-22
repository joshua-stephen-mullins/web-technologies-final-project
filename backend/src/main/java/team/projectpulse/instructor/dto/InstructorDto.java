package team.projectpulse.instructor.dto;

import java.util.List;

// Owner: Whitney (Person 3)
public record InstructorDto(Integer id, String firstName, String lastName, String email, boolean enabled, List<String> teamNames) {}
