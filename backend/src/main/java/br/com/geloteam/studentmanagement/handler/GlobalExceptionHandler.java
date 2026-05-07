package br.com.geloteam.studentmanagement.handler;

import br.com.geloteam.studentmanagement.DTO.ErrorDTO;
import br.com.geloteam.studentmanagement.exception.EntityIdNotExistsOrDelete;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import br.com.geloteam.studentmanagement.exception.SubscriptionAlreadyExists;
import br.com.geloteam.studentmanagement.exception.SubscriptionPendingPayament;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<ErrorDTO> handleEntityReturn(EntityNotFound ex){
        return ResponseEntity.status(404).body(new ErrorDTO(404, ex.getMessage()));
    }

    @ExceptionHandler(SubscriptionAlreadyExists.class)
    public ResponseEntity<ErrorDTO> handleEntityIdNotExists(EntityIdNotExistsOrDelete ex){
        return ResponseEntity.status(404).body(new ErrorDTO(404, ex.getMessage()));
    }

    @ExceptionHandler(SubscriptionAlreadyExists.class)
    public ResponseEntity<ErrorDTO> handleSubscriptionAlreadyExists(SubscriptionAlreadyExists ex){
        return ResponseEntity.status(409).body(new ErrorDTO(409,ex.getMessage()));
    }

    @ExceptionHandler(SubscriptionPendingPayament.class)
    public ResponseEntity<ErrorDTO> handleSubscriptionPendingPayament(SubscriptionPendingPayament ex){
        return ResponseEntity.status(400).body(new ErrorDTO(400,ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenerico(Exception ex) {
        return ResponseEntity.status(500).body(new ErrorDTO(500, "Erro interno no servidor"));
    }

}
