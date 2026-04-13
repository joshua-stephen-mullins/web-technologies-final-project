package team.projectpulse.system.exception;

// Shared - thrown when password change validation fails
public class PasswordChangeIllegalArgumentException extends RuntimeException {
    public PasswordChangeIllegalArgumentException(String message) {
        super(message);
    }
}
