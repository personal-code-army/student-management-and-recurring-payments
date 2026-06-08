package br.com.geloteam.studentmanagement.domain.user.port.in;

public interface ResetPasswordUseCase {
    void resetPassword(String token, String newPassword);
}
