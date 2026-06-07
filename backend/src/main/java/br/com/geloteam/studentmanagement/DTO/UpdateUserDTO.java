package br.com.geloteam.studentmanagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserDTO(
        @NotBlank(message = "name não pode ser vazio")
        String name,

        @Email(message = "email inválido")
        String email,

        String cellphoneNumber,

        @NotNull(message = "companyId não pode ser nulo")
        Long companyId
) {}
