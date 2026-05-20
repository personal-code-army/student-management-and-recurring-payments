package br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto;

public record PreferenceItem(
        String title,
        Integer quantity,
        String currency_id,
        Double unit_price
) {}
