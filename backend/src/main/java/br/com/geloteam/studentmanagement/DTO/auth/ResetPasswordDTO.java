package br.com.geloteam.studentmanagement.DTO.auth;

import br.com.geloteam.studentmanagement.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDTO(
        @NotBlank(message = "token não pode ser vazio")
        String token,

        @NotBlank(message = "newPassword não pode ser vazio")
        @ValidPassword
        String newPassword
) {}
