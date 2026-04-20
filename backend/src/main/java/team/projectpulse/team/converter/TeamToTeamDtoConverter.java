package team.projectpulse.team.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.team.Team;
import team.projectpulse.team.dto.TeamDto;

// Owner: Oscar (Person 2)
@Component
public class TeamToTeamDtoConverter implements Converter<Team, TeamDto> {

    @Override
    public TeamDto convert(Team source) {
        Integer sectionId = source.getSection() != null ? source.getSection().getId() : null;
        return new TeamDto(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getWebsiteUrl(),
                sectionId
        );
    }
}
