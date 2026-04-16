package team.projectpulse.section.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.section.Week;
import team.projectpulse.section.dto.WeekDto;

@Component
public class WeekToWeekDtoConverter implements Converter<Week, WeekDto> {

    @Override
    public WeekDto convert(Week source) {
        return new WeekDto(
                source.getId(),
                source.getWeekNumber(),
                source.getStartDate().toString(),
                source.getEndDate().toString(),
                source.isActive()
        );
    }
}
