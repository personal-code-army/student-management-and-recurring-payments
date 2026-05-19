package br.com.geloteam.studentmanagement.infrastructure.web.user.dto;

import br.com.geloteam.studentmanagement.domain.user.entity.User;

public record UserResponse(Long id, String name, String email, String cellphoneNumber, Long companyId) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCellphoneNumber(),
                user.getCompany().getId()
        );
    }
}
