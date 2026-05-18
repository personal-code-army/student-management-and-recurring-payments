package br.com.geloteam.studentmanagement.shared.exception;

public class NotFoundException extends AppException {

    public NotFoundException(String message) {
        super("ENTITY_NOT_FOUND", message);
    }
}
