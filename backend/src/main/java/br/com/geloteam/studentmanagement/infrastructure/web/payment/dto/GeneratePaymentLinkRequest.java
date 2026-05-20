package br.com.geloteam.studentmanagement.infrastructure.web.payment.dto;

import jakarta.validation.constraints.NotNull;

public record GeneratePaymentLinkRequest(
        @NotNull(message = "Assinatura não informada") Long subscriptionId
) {}
