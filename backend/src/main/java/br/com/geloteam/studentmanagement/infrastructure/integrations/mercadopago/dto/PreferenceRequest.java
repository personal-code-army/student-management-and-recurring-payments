package br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PreferenceRequest(
        List<PreferenceItem> items,
        String external_reference,
        String notification_url,
        String date_of_expiration
) {}
