package br.com.geloteam.studentmanagement.handler;

import br.com.geloteam.studentmanagement.DTO.ErrorDTO;
import br.com.geloteam.studentmanagement.exception.EntityIdNotExistsOrDelete;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import br.com.geloteam.studentmanagement.exception.SubscriptionAlreadyExists;
import br.com.geloteam.studentmanagement.exception.SubscriptionPendingPayament;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
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

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDTO> handleValidation(ValidationException ex) {
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

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> handleResponseStatus(ResponseStatusException ex) {
        int status = ex.getStatusCode().value();
        return ResponseEntity.status(status).body(new ErrorDTO(status, ex.getReason()));
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleUnauthorized(RuntimeException ex) {
        return ResponseEntity.status(401).body(new ErrorDTO(401, "Credenciais inválidas"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleBeanValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst().orElse("Dados inválidos");
        return ResponseEntity.status(400).body(new ErrorDTO(400, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenerico(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(500).body(new ErrorDTO(500, "Erro interno no servidor"));
    }

}
