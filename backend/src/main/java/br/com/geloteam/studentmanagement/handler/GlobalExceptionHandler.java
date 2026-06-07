package br.com.geloteam.studentmanagement.handler;

import br.com.geloteam.studentmanagement.DTO.ErrorDTO;
import br.com.geloteam.studentmanagement.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<ErrorDTO> handleEntityNotFound(EntityNotFound ex) {
        return ResponseEntity.status(404).body(new ErrorDTO(404, ex.getMessage()));
    }

    @ExceptionHandler(EntityIdNotExistsOrDelete.class)
    public ResponseEntity<ErrorDTO> handleEntityIdNotExists(EntityIdNotExistsOrDelete ex) {
        return ResponseEntity.status(404).body(new ErrorDTO(404, ex.getMessage()));
    }

    @ExceptionHandler(SubscriptionAlreadyExists.class)
    public ResponseEntity<ErrorDTO> handleSubscriptionAlreadyExists(SubscriptionAlreadyExists ex) {
        return ResponseEntity.status(409).body(new ErrorDTO(409, ex.getMessage()));
    }

    @ExceptionHandler(SubscriptionPendingPayament.class)
    public ResponseEntity<ErrorDTO> handleSubscriptionPendingPayament(SubscriptionPendingPayament ex) {
        return ResponseEntity.status(400).body(new ErrorDTO(400, ex.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorDTO> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.status(400).body(new ErrorDTO(400, ex.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorDTO> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity.status(400).body(new ErrorDTO(400, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(422).body(new ErrorDTO(422, errors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDTO> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(401).body(new ErrorDTO(401, "Credenciais inválidas"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuthentication(AuthenticationException ex) {
        return ResponseEntity.status(401).body(new ErrorDTO(401, ex.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> handleResponseStatus(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        return ResponseEntity.status(status).body(new ErrorDTO(status, ex.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenerico(Exception ex) {
        return ResponseEntity.status(500).body(new ErrorDTO(500, "Erro interno no servidor"));
    }
}
