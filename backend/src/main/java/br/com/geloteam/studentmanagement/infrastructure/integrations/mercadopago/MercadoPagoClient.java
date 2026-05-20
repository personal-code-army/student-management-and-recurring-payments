package br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago;

import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.MpPaymentResource;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.PreferenceRequest;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.PreferenceResponse;
import br.com.geloteam.studentmanagement.shared.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@Configuration
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
                        log.error("Mercado Pago createPreference failed: status={} body={}",
                                res.getStatusCode(), new String(res.getBody().readAllBytes()));
                        throw new MercadoPagoApiException("MP_CREATE_PREFERENCE_FAILED",
                                "Falha ao criar preferência no Mercado Pago");
                    })
                    .body(PreferenceResponse.class);
        } catch (MercadoPagoApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Mercado Pago createPreference unexpected error", e);
            throw new MercadoPagoApiException("MP_UNEXPECTED_ERROR",
                    "Erro inesperado ao contatar o Mercado Pago");
        }
    }

    public MpPaymentResource fetchPayment(String paymentId) {
        try {
            return restClient.get()
                    .uri("/v1/payments/{id}", paymentId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        log.error("Mercado Pago fetchPayment failed: status={}", res.getStatusCode());
                        throw new MercadoPagoApiException("MP_FETCH_PAYMENT_FAILED",
                                "Falha ao consultar pagamento no Mercado Pago");
                    })
                    .body(MpPaymentResource.class);
        } catch (MercadoPagoApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Mercado Pago fetchPayment unexpected error", e);
            throw new MercadoPagoApiException("MP_UNEXPECTED_ERROR",
                    "Erro inesperado ao contatar o Mercado Pago");
        }
    }

    public static class MercadoPagoApiException extends AppException {
        public MercadoPagoApiException(String code, String message) {
            super(code, message);
        }
    }
}
