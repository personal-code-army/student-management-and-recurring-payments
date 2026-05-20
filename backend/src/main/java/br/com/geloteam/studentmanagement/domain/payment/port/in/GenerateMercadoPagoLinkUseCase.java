package br.com.geloteam.studentmanagement.domain.payment.port.in;

public interface GenerateMercadoPagoLinkUseCase {

    PaymentLinkResult execute(Long subscriptionId);

    record PaymentLinkResult(String checkoutUrl, String expirationDate, String externalReference) {}
}
