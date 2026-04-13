package br.com.geloteam.studentmanagement.DTO.auth;

import br.com.geloteam.studentmanagement.Models.User;

public record RegisterResponseDTO(
        String name,
        String email,
        String cellphoneNumber,
        Long companyId
) {
    public RegisterResponseDTO(User user) {
        this(
                user.getName(),
                user.getEmail(),
                user.getCellphoneNumber(),
                user.getCompany().getId()
        );
    }
}
