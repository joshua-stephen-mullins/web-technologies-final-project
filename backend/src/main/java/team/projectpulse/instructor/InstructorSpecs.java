package team.projectpulse.instructor;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class InstructorSpecs {

    private InstructorSpecs() {}

    public static Specification<Instructor> hasFirstName(String firstName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
    }

    public static Specification<Instructor> hasLastName(String lastName) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
    }

    public static Specification<Instructor> isEnabled(boolean enabled) {
        return (root, query, cb) -> cb.equal(root.get("enabled"), enabled);
    }

    public static Specification<Instructor> hasRole(String role) {
        return (root, query, cb) ->
                cb.like(root.get("roles"), "%" + role + "%");
    }

    public static Specification<Instructor> hasTeamName(String teamName) {
        return (root, query, cb) -> {
            query.distinct(true);
            var teamJoin = root.join("teams", JoinType.INNER);
            return cb.like(cb.lower(teamJoin.get("name")), "%" + teamName.toLowerCase() + "%");
        };
    }
}