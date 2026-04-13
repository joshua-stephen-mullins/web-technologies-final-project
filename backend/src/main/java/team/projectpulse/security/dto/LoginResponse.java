package team.projectpulse.security.dto;

// Owner: Josh (Person 1)
// Response body for POST /api/auth/login
public record LoginResponse(String token, String username, String roles) {}
