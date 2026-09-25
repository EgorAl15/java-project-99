package hexlet.code.app.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleResourceNotFound(
      ResourceNotFoundException exception) {

    log.warn("Resource not found: {}", exception.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Map.of("error", exception.getMessage()));
  }

  @ExceptionHandler(ResourceConflictException.class)
  public ResponseEntity<Map<String, String>> handleResourceConflict(
      ResourceConflictException exception) {

    log.warn("Resource conflict: {}", exception.getMessage());

    return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
      DataIntegrityViolationException exception) {

    log.warn("Data integrity violation", exception);

    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(Map.of("error", "Data integrity violation"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidation(
      MethodArgumentNotValidException exception) {

    var fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst();

    var message =
        fieldError
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .orElse("Validation failed");

    log.warn("Validation failed: {}", message);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolation(
      ConstraintViolationException exception) {

    log.warn("Constraint violation: {}", exception.getMessage());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Map.of("error", exception.getMessage()));
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleBadCredentials(
      BadCredentialsException exception) {

    log.warn("Authentication failed");

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, String>> handleAuthentication(
      AuthenticationException exception) {

    log.warn("Authentication failed");

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException exception) {

    log.warn("Access denied");

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Forbidden"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleException(Exception exception) {

    log.error("Unexpected application error", exception);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "Internal server error"));
  }
}
