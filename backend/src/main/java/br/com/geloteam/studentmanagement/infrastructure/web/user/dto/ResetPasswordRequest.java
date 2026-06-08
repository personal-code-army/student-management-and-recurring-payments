package br.com.geloteam.studentmanagement.infrastructure.web.user.dto;

import br.com.geloteam.studentmanagement.shared.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "token não pode ser vazio") String token,
        @NotBlank(message = "newPassword não pode ser vazio") @ValidPassword String newPassword
) {}
