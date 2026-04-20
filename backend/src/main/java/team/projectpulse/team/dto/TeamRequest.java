package team.projectpulse.team.dto;

import jakarta.validation.constraints.NotBlank;

// Owner: Oscar (Person 2)
// Request body for UC-9: Create team and UC-10: Edit team
// sectionId required for create, ignored on edit
public record TeamRequest(
        @NotBlank String name,
        String description,
        String websiteUrl,
        Integer sectionId
) {}
