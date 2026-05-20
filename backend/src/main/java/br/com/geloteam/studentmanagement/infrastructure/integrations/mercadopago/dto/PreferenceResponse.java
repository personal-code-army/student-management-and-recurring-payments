package br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PreferenceResponse(
        String id,
        String init_point,
        String sandbox_init_point,
        String date_of_expiration,
        String external_reference
) {}
