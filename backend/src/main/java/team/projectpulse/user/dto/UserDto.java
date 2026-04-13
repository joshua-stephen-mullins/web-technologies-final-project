package team.projectpulse.user.dto;

// Owner: Josh (Person 1)
// Safe outbound representation of a user (no password field)
public record UserDto(Integer id, String username, String firstName, String lastName, String roles, boolean enabled) {}
