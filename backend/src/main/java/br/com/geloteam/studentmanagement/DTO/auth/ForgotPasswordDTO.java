package br.com.geloteam.studentmanagement.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
        @NotBlank(message = "email não pode ser vazio")
        @Email(message = "email inválido")
        String email
) {}
