package team.projectpulse.activity.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.activity.Activity;
import team.projectpulse.activity.dto.ActivityDto;

// Owner: Oscar (Person 2)
@Component
public class ActivityToActivityDtoConverter implements Converter<Activity, ActivityDto> {

    @Override
    public ActivityDto convert(Activity source) {
        String studentName = source.getStudent() != null
                ? source.getStudent().getFirstName() + " " + source.getStudent().getLastName()
                : null;
        return new ActivityDto(
                source.getId(),
                source.getStudent() != null ? source.getStudent().getId() : null,
                studentName,
                source.getWeek() != null ? source.getWeek().getId() : null,
                source.getCategory(),
                source.getDescription(),
                source.getPlannedHours() != null ? source.getPlannedHours().doubleValue() : null,
                source.getActualHours() != null ? source.getActualHours().doubleValue() : null,
                source.getStatus()
        );
    }
}
