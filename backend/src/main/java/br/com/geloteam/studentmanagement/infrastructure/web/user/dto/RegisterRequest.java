package br.com.geloteam.studentmanagement.infrastructure.web.user.dto;

import br.com.geloteam.studentmanagement.shared.validation.ValidCpf;
import br.com.geloteam.studentmanagement.shared.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(
        @NotBlank(message = "name não pode ser vazio") String name,
        @NotBlank(message = "email não pode ser vazio") @Email(message = "email inválido") String email,
        @NotBlank(message = "password não pode ser vazio") @ValidPassword String password,
        @ValidCpf String cpf,
        String cellphoneNumber,
        @NotNull(message = "companyId não pode ser nulo") Long companyId
) {}
