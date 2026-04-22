package team.projectpulse.instructor.dto;

import java.util.List;

// Owner: Whitney (Person 3)
public record InstructorDetailDto(
        Integer id,
        String firstName,
        String lastName,
        String email,
        boolean enabled,
        List<SectionTeams> teamsBySection
) {
    public record SectionTeams(Integer sectionId, String sectionName, List<TeamSummary> teams) {}
    public record TeamSummary(Integer id, String name) {}
}
