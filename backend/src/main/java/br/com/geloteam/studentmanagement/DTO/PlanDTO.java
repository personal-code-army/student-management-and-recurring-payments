package br.com.geloteam.studentmanagement.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PlanDTO(
        @NotBlank(message = "Nome não informado") String name,
        @NotNull(message = "Valor mensal não informado") Double monthly_amount,
        int frequency
) {
}
