package br.com.geloteam.studentmanagement.domain.user.port.in;

public interface ChangePasswordUseCase {
    void execute(String email, String currentPassword, String newPassword);
}
