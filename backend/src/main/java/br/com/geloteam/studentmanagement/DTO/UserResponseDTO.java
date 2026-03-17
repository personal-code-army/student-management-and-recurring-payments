package br.com.geloteam.studentmanagement.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserResponseDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        String cellphoneNumber,
        @NotNull Long companyId
) {
    // to-do
}
