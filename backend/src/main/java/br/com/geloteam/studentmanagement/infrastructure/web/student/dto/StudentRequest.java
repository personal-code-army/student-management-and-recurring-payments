package br.com.geloteam.studentmanagement.infrastructure.web.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentRequest(
        @NotBlank String name,
        @NotBlank String cpf,
        @NotNull LocalDate birthDate,
        String phone,
        String email,
        String address,
        Long planId,
        boolean active,
        String paymentMethod
) {}
