package team.projectpulse.section;

import org.springframework.data.jpa.domain.Specification;

public class SectionSpecs {

    private SectionSpecs() {}

    public static Specification<Section> hasSectionName(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
