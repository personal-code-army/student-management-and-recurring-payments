package br.com.geloteam.studentmanagement.infrastructure.web.payment.dto;

public record PaymentLinkResponse(
        String checkoutUrl,
        String expirationDate,
        String externalReference
) {}
