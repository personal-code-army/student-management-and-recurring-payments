package br.com.geloteam.studentmanagement.infrastructure.web.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PaymentRequest(
        @NotBlank(message = "Descrição não informada") String description,
        @NotNull(message = "Valor não informado") Double value,
        @NotBlank(message = "Forma de pagamento não informada") String paymentMethod,
        @NotNull @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate dueDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") LocalDate issueDate,
        String status,
        @NotNull(message = "Assinatura não informada") Long subscriptionId
) {}
