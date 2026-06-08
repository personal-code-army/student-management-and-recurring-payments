package br.com.geloteam.studentmanagement.domain.user.port.in;

public interface ForgotPasswordUseCase {
    String generateResetToken(String email);
}
