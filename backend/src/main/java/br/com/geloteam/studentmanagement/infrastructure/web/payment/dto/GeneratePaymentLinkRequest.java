package br.com.geloteam.studentmanagement.infrastructure.web.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GeneratePaymentLinkRequest(
        @NotNull(message = "Assinatura não informada")
        @Positive(message = "ID da assinatura deve ser positivo")
        Long subscriptionId
) {}
