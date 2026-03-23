package br.com.geloteam.studentmanagement.DTO;

import br.com.geloteam.studentmanagement.Models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserResponseDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        String cellphoneNumber,
        @NotNull Long companyId
) {
    public UserResponseDTO(User user) {
        this(
                user.getName(),
                user.getEmail(),
                user.getCellphoneNumber(),
                user.getCompany().getId()
        );
    }
}
