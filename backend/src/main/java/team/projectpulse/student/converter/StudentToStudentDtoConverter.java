package team.projectpulse.student.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import team.projectpulse.student.Student;
import team.projectpulse.student.dto.StudentDto;

// Owner: Oscar (Person 2)
// teamName and sectionName are resolved by StudentService when needed
@Component
public class StudentToStudentDtoConverter implements Converter<Student, StudentDto> {

    @Override
    public StudentDto convert(Student source) {
        return new StudentDto(
                source.getId(),
                source.getFirstName(),
                source.getLastName(),
                source.getUsername(),
                null,
                null
        );
    }
}
