package br.com.geloteam.studentmanagement.infrastructure.web.payment;

import br.com.geloteam.studentmanagement.application.payment.MercadoPagoWebhookHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments/webhook")
public class MercadoPagoWebhookController {

    private final MercadoPagoWebhookHandler handler;

    public MercadoPagoWebhookController(MercadoPagoWebhookHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/mercadopago")
    public ResponseEntity<Void> handle(
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestHeader(value = "x-request-id", required = false) String requestId,
            @RequestBody Map<String, Object> body
    ) {
        log.info("Received Mercado Pago webhook: type={}", body.get("type"));
        handler.process(signature, requestId, body);
        return ResponseEntity.ok().build();
    }
}
