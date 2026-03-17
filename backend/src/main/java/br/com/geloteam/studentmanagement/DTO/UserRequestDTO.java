package br.com.geloteam.studentmanagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        String cellphoneNumber,
        @NotNull Long companyId
) {
    // to-do
}
