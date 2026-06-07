package br.com.geloteam.studentmanagement.infrastructure.web.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanRequest(
        @NotBlank(message = "Nome não informado") String name,
        @NotNull(message = "Valor mensal não informado") Double monthlyAmount,
        Integer frequency
) {}
