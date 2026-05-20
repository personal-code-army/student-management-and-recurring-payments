package br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago;

import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.MpPaymentResource;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.PreferenceRequest;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.PreferenceResponse;
import br.com.geloteam.studentmanagement.shared.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@EnableConfigurationProperties(MercadoPagoProperties.class)
public class MercadoPagoClient {

    private final RestClient restClient;
    private final MercadoPagoProperties props;

    public MercadoPagoClient(MercadoPagoProperties props) {
        this.props = props;
        this.restClient = RestClient.builder()
                .baseUrl(props.apiBaseUrl())
                .defaultHeader("Authorization", "Bearer " + props.accessToken())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public PreferenceResponse createPreference(PreferenceRequest request) {
        try {
            return restClient.post()
                    .uri("/checkout/preferences")
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String responseBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        int status = res.getStatusCode().value();
                        log.error("Mercado Pago createPreference failed: status={} body={}", status, responseBody);
                        throw new MercadoPagoApiException("MP_CREATE_PREFERENCE_FAILED",
                                "Falha ao criar preferência no Mercado Pago (status=" + status + "): " + responseBody);
                    })
                    .body(PreferenceResponse.class);
        } catch (MercadoPagoApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Mercado Pago createPreference unexpected error", e);
            throw new MercadoPagoApiException("MP_UNEXPECTED_ERROR",
                    "Erro inesperado ao contatar o Mercado Pago: " + e.getMessage());
        }
    }

    public MpPaymentResource fetchPayment(String paymentId) {
        try {
            return restClient.get()
                    .uri("/v1/payments/{id}", paymentId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        String responseBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        int status = res.getStatusCode().value();
                        log.error("Mercado Pago fetchPayment failed: status={} body={}", status, responseBody);
                        throw new MercadoPagoApiException("MP_FETCH_PAYMENT_FAILED",
                                "Falha ao consultar pagamento no Mercado Pago (status=" + status + "): " + responseBody);
                    })
                    .body(MpPaymentResource.class);
        } catch (MercadoPagoApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Mercado Pago fetchPayment unexpected error", e);
            throw new MercadoPagoApiException("MP_UNEXPECTED_ERROR",
                    "Erro inesperado ao contatar o Mercado Pago: " + e.getMessage());
        }
    }

    public static class MercadoPagoApiException extends AppException {
        public MercadoPagoApiException(String code, String message) {
            super(code, message);
        }
    }
}
