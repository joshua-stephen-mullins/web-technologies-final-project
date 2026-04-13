package team.projectpulse.security.dto;

// Owner: Josh (Person 1)
// Request body for POST /api/auth/login
public record LoginRequest(String username, String password) {}
