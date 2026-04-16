package team.projectpulse.user.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.user.PeerEvaluationUser;
import team.projectpulse.user.dto.UserDto;

@Component
public class UserToUserDtoConverter implements Converter<PeerEvaluationUser, UserDto> {

    @Override
    public UserDto convert(PeerEvaluationUser source) {
        return new UserDto(
                source.getId(),
                source.getUsername(),
                source.getFirstName(),
                source.getLastName(),
                source.getRoles(),
                source.isEnabled()
        );
    }
}
