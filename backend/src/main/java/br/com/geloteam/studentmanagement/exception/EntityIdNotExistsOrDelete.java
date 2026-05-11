package br.com.geloteam.studentmanagement.exception;

public class EntityIdNotExistsOrDelete extends RuntimeException {
    public EntityIdNotExistsOrDelete(String message) {
        super(message);
    }
}
