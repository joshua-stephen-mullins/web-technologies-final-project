package team.projectpulse.student;

import org.springframework.data.jpa.domain.Specification;

// Owner: Oscar (Person 2)
// JPA Specifications for UC-15: find students by name, email, section, team
public class StudentSpecs {

    private StudentSpecs() {}

    public static Specification<Student> hasRole(String role) {
        return (root, query, cb) -> cb.like(root.get("roles"), "%" + role + "%");
    }

    public static Specification<Student> hasFirstName(String firstName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Student> hasLastName(String lastName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Student> hasEmail(String email) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("username")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<Student> hasTeamId(Integer teamId) {
        return (root, query, cb) -> cb.equal(root.get("teamId"), teamId);
    }
}
