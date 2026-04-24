package team.projectpulse.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRegistrationRequest(
        @NotBlank String firstName,
        @Pattern(regexp = "[A-Za-z]?", message = "Middle initial must be a single letter") String middleInitial,
        @NotBlank String lastName,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
) {}
