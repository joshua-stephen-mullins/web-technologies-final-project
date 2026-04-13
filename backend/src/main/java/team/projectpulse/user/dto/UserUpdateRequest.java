package team.projectpulse.user.dto;

// Owner: Josh (Person 1)
// Request body for UC-26: Edit own account
public record UserUpdateRequest(String firstName, String lastName, String email) {}
