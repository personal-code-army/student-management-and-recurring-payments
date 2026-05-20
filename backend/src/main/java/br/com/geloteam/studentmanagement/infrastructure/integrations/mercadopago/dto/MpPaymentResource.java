package br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MpPaymentResource(
        Long id,
        String status,
        String status_detail,
        String external_reference,
        Payer payer
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Payer(String email, String first_name, String last_name) {}
}
