package br.com.geloteam.studentmanagement.shared.exception;

public class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super("AUTH_INVALID_CREDENTIALS", message);
    }

    public UnauthorizedException(String code, String message) {
        super(code, message);
    }
}
