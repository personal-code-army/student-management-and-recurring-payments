package br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mercadopago")
public record MercadoPagoProperties(
        String accessToken,
        String webhookSecret,
        String apiBaseUrl,
        String notificationUrl
) {}
