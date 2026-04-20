package team.projectpulse.team;

import org.springframework.data.jpa.domain.Specification;

// Owner: Oscar (Person 2)
// JPA Specifications for UC-7: find teams by sectionId, name
public class TeamSpecs {

    private TeamSpecs() {}

    public static Specification<Team> hasName(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Team> hasSectionId(Integer sectionId) {
        return (root, query, cb) ->
                cb.equal(root.get("section").get("id"), sectionId);
    }
}
