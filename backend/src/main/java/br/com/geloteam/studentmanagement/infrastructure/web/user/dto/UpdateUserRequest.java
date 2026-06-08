package br.com.geloteam.studentmanagement.infrastructure.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank(message = "name não pode ser vazio") String name,
        @NotBlank(message = "email não pode ser vazio") @Email(message = "email inválido") String email,
        String cellphoneNumber,
        @NotNull(message = "companyId não pode ser nulo") Long companyId
) {}
