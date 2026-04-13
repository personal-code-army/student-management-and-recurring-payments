package br.com.geloteam.studentmanagement.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequestDTO(
        @NotBlank(message = "name can't be null or empty")
        String name,

        @NotBlank(message = "email can't be null or empty")
        @Email String email,

        @NotBlank(message = "password can't be null or empty")
        String password,

        String cellphoneNumber,

        @NotNull(message = "company id can't be null or empty")
        Long companyId
) {

}
