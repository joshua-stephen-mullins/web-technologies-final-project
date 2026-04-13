package team.projectpulse.system.exception;

// Shared - thrown when a requested entity is not found (results in 404)
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String objectName, Object id) {
        super("Could not find " + objectName + " with id " + id);
    }
}
