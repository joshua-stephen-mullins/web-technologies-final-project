package team.projectpulse.team.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.team.Team;
import team.projectpulse.team.dto.TeamDto;

import java.util.List;

// Owner: Oscar (Person 2)
@Component
public class TeamToTeamDtoConverter implements Converter<Team, TeamDto> {

    @Override
    public TeamDto convert(Team source) {
        Integer sectionId = source.getSection() != null ? source.getSection().getId() : null;

        List<TeamDto.MemberDto> students = source.getStudents().stream()
                .map(u -> new TeamDto.MemberDto(u.getId(), u.getFirstName(), u.getLastName()))
                .toList();

        List<TeamDto.MemberDto> instructors = source.getInstructors().stream()
                .map(u -> new TeamDto.MemberDto(u.getId(), u.getFirstName(), u.getLastName()))
                .toList();

        return new TeamDto(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getWebsiteUrl(),
                sectionId,
                students,
                instructors
        );
    }
}
