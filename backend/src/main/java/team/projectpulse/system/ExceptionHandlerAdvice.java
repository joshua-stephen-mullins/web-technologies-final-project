package team.projectpulse.system;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import team.projectpulse.system.exception.ObjectNotFoundException;
import team.projectpulse.system.exception.ObjectAlreadyExistsException;

// Shared - global exception handler for all controllers
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ObjectNotFoundException.class)
    public Result handleObjectNotFoundException(ObjectNotFoundException ex) {
        // TODO: Implement
        return new Result(false, StatusCode.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ObjectAlreadyExistsException.class)
    public Result handleObjectAlreadyExistsException(ObjectAlreadyExistsException ex) {
        // TODO: Implement
        return new Result(false, StatusCode.CONFLICT, ex.getMessage());
    }

    // TODO: Add handlers for MethodArgumentNotValidException, AccessDeniedException, etc.
}
