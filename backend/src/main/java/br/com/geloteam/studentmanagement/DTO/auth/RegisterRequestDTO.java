package br.com.geloteam.studentmanagement.DTO.auth;

import br.com.geloteam.studentmanagement.validation.ValidCpf;
import br.com.geloteam.studentmanagement.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
        @NotBlank(message = "name não pode ser vazio")
        String name,

        @NotBlank(message = "email não pode ser vazio")
        @Email(message = "email inválido")
        String email,

        @NotBlank(message = "password não pode ser vazio")
        @ValidPassword
        String password,

        @NotBlank(message = "cpf não pode ser vazio")
        @ValidCpf
        String cpf,

        String cellphoneNumber,

        @NotNull(message = "companyId não pode ser nulo")
        Long companyId
) {}
