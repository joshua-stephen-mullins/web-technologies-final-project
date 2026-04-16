package team.projectpulse.section.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.section.Section;
import team.projectpulse.section.dto.SectionDto;

@Component
public class SectionToSectionDtoConverter implements Converter<Section, SectionDto> {

    @Override
    public SectionDto convert(Section source) {
        Integer rubricId = source.getRubric() != null ? source.getRubric().getId() : null;
        String rubricName = source.getRubric() != null ? source.getRubric().getName() : null;
        return new SectionDto(
                source.getId(),
                source.getName(),
                source.getStartDate().toString(),
                source.getEndDate().toString(),
                rubricId,
                rubricName
        );
    }
}
