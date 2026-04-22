package team.projectpulse.instructor.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.instructor.Instructor;
import team.projectpulse.instructor.dto.InstructorDto;

import java.util.List;

// Owner: Whitney (Person 3)
@Component
public class InstructorToInstructorDtoConverter implements Converter<Instructor, InstructorDto> {

    @Override
    public InstructorDto convert(Instructor source) {
        return new InstructorDto(
                source.getId(),
                source.getFirstName(),
                source.getLastName(),
                source.getUsername(),
                source.isEnabled(),
                List.of()
        );
    }
}
