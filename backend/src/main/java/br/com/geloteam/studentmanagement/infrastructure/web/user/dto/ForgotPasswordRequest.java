package br.com.geloteam.studentmanagement.infrastructure.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "email não pode ser vazio") @Email(message = "email inválido") String email
) {}
