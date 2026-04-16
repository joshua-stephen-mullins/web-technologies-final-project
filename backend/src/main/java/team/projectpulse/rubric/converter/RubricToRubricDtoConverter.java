package team.projectpulse.rubric.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.rubric.Rubric;
import team.projectpulse.rubric.dto.CriterionDto;
import team.projectpulse.rubric.dto.RubricDto;

import java.util.List;

@Component
public class RubricToRubricDtoConverter implements Converter<Rubric, RubricDto> {

    @Override
    public RubricDto convert(Rubric source) {
        List<CriterionDto> criteriaDto = source.getCriteria().stream()
                .map(c -> new CriterionDto(c.getId(), c.getName(), c.getDescription(), c.getMaxScore(), c.getSortOrder()))
                .toList();
        return new RubricDto(source.getId(), source.getName(), criteriaDto);
    }
}
