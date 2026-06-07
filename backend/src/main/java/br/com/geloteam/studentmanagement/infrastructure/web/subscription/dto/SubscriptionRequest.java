package br.com.geloteam.studentmanagement.infrastructure.web.subscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SubscriptionRequest(
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate startDate,
        @NotBlank(message = "Status não informado") String status,
        @NotBlank(message = "Forma de pagamento não informada") String paymentMethod,
        @NotNull(message = "Plano não informado") Long planId,
        @NotNull(message = "Aluno não informado") Long studentId
) {}
