package br.com.geloteam.studentmanagement.DTO.auth;

import br.com.geloteam.studentmanagement.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDTO(
        @NotBlank(message = "currentPassword não pode ser vazio")
        String currentPassword,

        @NotBlank(message = "newPassword não pode ser vazio")
        @ValidPassword
        String newPassword
) {}
