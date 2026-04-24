package team.projectpulse.team.dto;

import java.util.List;

// Owner: Oscar (Person 2)
public record TeamDto(
        Integer id,
        String name,
        String description,
        String websiteUrl,
        Integer sectionId,
        List<MemberDto> students,
        List<MemberDto> instructors
) {
    public record MemberDto(Integer id, String firstName, String lastName) {}
}
