package br.com.geloteam.studentmanagement.shared.exception;

public class BadRequestException extends AppException {

    public BadRequestException(String code, String message) {
        super(code, message);
    }
}
