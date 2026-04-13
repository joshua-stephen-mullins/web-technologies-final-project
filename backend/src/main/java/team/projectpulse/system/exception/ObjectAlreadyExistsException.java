package team.projectpulse.system.exception;

// Shared - thrown when a duplicate unique entity is created (results in 409)
public class ObjectAlreadyExistsException extends RuntimeException {
    public ObjectAlreadyExistsException(String message) {
        super(message);
    }
}
