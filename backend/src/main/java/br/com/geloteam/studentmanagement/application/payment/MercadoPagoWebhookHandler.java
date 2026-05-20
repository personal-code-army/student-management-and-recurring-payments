package br.com.geloteam.studentmanagement.application.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.MercadoPagoClient;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.MercadoPagoProperties;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.MpPaymentResource;
import br.com.geloteam.studentmanagement.shared.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MercadoPagoWebhookHandler {

    private final PaymentRepositoryPort paymentRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final MercadoPagoProperties props;

    public MercadoPagoWebhookHandler(PaymentRepositoryPort paymentRepository,
                                     MercadoPagoClient mercadoPagoClient,
                                     MercadoPagoProperties props) {
        this.paymentRepository = paymentRepository;
        this.mercadoPagoClient = mercadoPagoClient;
        this.props = props;
    }

    @Transactional
    public void process(String signatureHeader, String requestId, Map<String, Object> body) {
        String dataId = extractDataId(body);
        if (dataId == null) {
            log.warn("Webhook ignored: missing data.id in payload");
            return;
        }

        verifySignature(signatureHeader, requestId, dataId);

        String type = (String) body.get("type");
        if (!"payment".equalsIgnoreCase(type) && !"payment.updated".equalsIgnoreCase(type)) {
            log.info("Webhook ignored: unsupported type='{}'", type);
            return;
        }

        MpPaymentResource mpPayment;
        try {
            mpPayment = mercadoPagoClient.fetchPayment(dataId);
        } catch (Exception e) {
            log.error("fetchPayment failed for {}: {}", dataId, e.getMessage(), e);
            return;
        }
        if (mpPayment.external_reference() == null) {
            log.warn("Webhook payment {} has no external_reference; ignoring", dataId);
            return;
        }

        Optional<Payment> maybePayment = paymentRepository.findByExternalReference(mpPayment.external_reference());
        if (maybePayment.isEmpty()) {
            log.warn("Webhook payment {} references unknown external_reference={}",
                    dataId, mpPayment.external_reference());
            return;
        }

        Payment payment = maybePayment.get();
        payment.setMercadoPagoPaymentId(String.valueOf(mpPayment.id()));
        payment.setStatus(mapStatus(mpPayment.status()));
        if (mpPayment.payer() != null) {
            String fullName = joinNonBlank(mpPayment.payer().first_name(), mpPayment.payer().last_name());
            payment.setPayerName(fullName);
            payment.setPayerEmail(mpPayment.payer().email());
        }
        paymentRepository.save(payment);
        log.info("Payment {} updated from MP webhook: status={}", payment.getId(), payment.getStatus());
    }

    @SuppressWarnings("unchecked")
    private String extractDataId(Map<String, Object> body) {
        Object data = body.get("data");
        if (data instanceof Map<?, ?> dataMap) {
            Object id = ((Map<String, Object>) dataMap).get("id");
            return id == null ? null : String.valueOf(id);
        }
        return null;
    }

    private void verifySignature(String signatureHeader, String requestId, String dataId) {
        String secret = props.webhookSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "MERCADOPAGO_WEBHOOK_SECRET is not configured; webhook rejected");
        }
        if (signatureHeader == null || signatureHeader.isBlank()) {
            throw new UnauthorizedException("MP_WEBHOOK_MISSING_SIGNATURE",
                    "Assinatura do webhook ausente");
        }
        Map<String, String> parts = parseSignatureHeader(signatureHeader);
        String ts = parts.get("ts");
        String v1 = parts.get("v1");
        if (ts == null || v1 == null) {
            throw new UnauthorizedException("MP_WEBHOOK_INVALID_SIGNATURE",
                    "Formato de assinatura do webhook inválido");
        }
        String manifest = "id:" + dataId + ";"
                + (requestId != null ? "request-id:" + requestId + ";" : "")
                + "ts:" + ts + ";";
        String expected = hmacSha256Hex(secret, manifest);
        if (!constantTimeEquals(expected, v1)) {
            throw new UnauthorizedException("MP_WEBHOOK_INVALID_SIGNATURE",
                    "Assinatura do webhook não confere");
        }
    }

    private Map<String, String> parseSignatureHeader(String header) {
        java.util.Map<String, String> out = new java.util.HashMap<>();
        for (String part : header.split(",")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                out.put(kv[0].trim(), kv[1].trim());
            }
        }
        return out;
    }

    private String hmacSha256Hex(String secret, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(raw.length * 2);
            for (byte b : raw) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (Exception e) {
            log.error("HMAC calculation failed", e);
            throw new UnauthorizedException("MP_WEBHOOK_HMAC_ERROR",
                    "Falha ao calcular HMAC: " + e.getMessage());
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null || a.length() != b.length()) return false;
        int diff = 0;
        for (int i = 0; i < a.length(); i++) diff |= a.charAt(i) ^ b.charAt(i);
        return diff == 0;
    }

    private String mapStatus(String mpStatus) {
        if (mpStatus == null) return "pending";
        return switch (mpStatus) {
            case "approved" -> "approved";
            case "rejected", "cancelled" -> "rejected";
            case "refunded", "charged_back" -> "refunded";
            case "in_process", "pending", "authorized" -> "pending";
            default -> mpStatus;
        };
    }

    private String joinNonBlank(String a, String b) {
        if (a == null || a.isBlank()) return b == null ? null : b.trim();
        if (b == null || b.isBlank()) return a.trim();
        return (a.trim() + " " + b.trim());
    }
}
