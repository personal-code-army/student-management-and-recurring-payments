package br.com.geloteam.studentmanagement.infrastructure.web.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank(message = "name can't be null or empty") String name,
        String cellphoneNumber,
        @NotNull(message = "company id can't be null") Long companyId
) {}
