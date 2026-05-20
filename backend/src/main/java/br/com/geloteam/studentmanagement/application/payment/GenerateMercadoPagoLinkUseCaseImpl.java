package br.com.geloteam.studentmanagement.application.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.in.GenerateMercadoPagoLinkUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.out.SubscriptionRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.MercadoPagoClient;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.MercadoPagoProperties;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.PreferenceItem;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.PreferenceRequest;
import br.com.geloteam.studentmanagement.infrastructure.integrations.mercadopago.dto.PreferenceResponse;
import br.com.geloteam.studentmanagement.shared.exception.BadRequestException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class GenerateMercadoPagoLinkUseCaseImpl implements GenerateMercadoPagoLinkUseCase {

    private static final int LINK_EXPIRATION_DAYS = 7;

    private final SubscriptionRepositoryPort subscriptionRepository;
    private final PlanRepositoryPort planRepository;
    private final PaymentRepositoryPort paymentRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final MercadoPagoProperties mpProperties;

    public GenerateMercadoPagoLinkUseCaseImpl(SubscriptionRepositoryPort subscriptionRepository,
                                              PlanRepositoryPort planRepository,
                                              PaymentRepositoryPort paymentRepository,
                                              MercadoPagoClient mercadoPagoClient,
                                              MercadoPagoProperties mpProperties) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.paymentRepository = paymentRepository;
        this.mercadoPagoClient = mercadoPagoClient;
        this.mpProperties = mpProperties;
    }

    @Override
    @Transactional
    public PaymentLinkResult execute(Long subscriptionId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada com ID: " + subscriptionId));

        if (subscription.getStatus() != null && subscription.getStatus().equalsIgnoreCase("inactive")) {
            throw new BadRequestException("SUBSCRIPTION_INACTIVE",
                    "Não é possível gerar link para uma assinatura inativa");
        }

        Plan plan = planRepository.findById(subscription.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plano não encontrado com ID: " + subscription.getPlanId()));

        String externalReference = "SUB-" + subscription.getId() + "-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plusDays(LINK_EXPIRATION_DAYS);
        String expirationStr = expiration.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));

        Payment payment = new Payment();
        payment.setSubscriptionId(subscription.getId());
        payment.setDescription("Assinatura | " + plan.getName());
        payment.setValue(plan.getMonthlyAmount());
        payment.setPaymentMethod("mercadopago");
        payment.setDueDate(LocalDate.now().plusDays(LINK_EXPIRATION_DAYS));
        payment.setIssueDate(LocalDate.now());
        payment.setStatus("pending");
        payment.setExternalReference(externalReference);
        Payment saved = paymentRepository.save(payment);

        PreferenceRequest request = new PreferenceRequest(
                List.of(new PreferenceItem(plan.getName(), 1, "BRL", plan.getMonthlyAmount())),
                externalReference,
                mpProperties.notificationUrl(),
                expirationStr
        );
        PreferenceResponse mpResponse = mercadoPagoClient.createPreference(request);

        saved.setMercadoPagoPreferenceId(mpResponse.id());
        saved.setCheckoutUrl(mpResponse.init_point());
        Payment finalPayment = paymentRepository.save(saved);
        log.info("Mercado Pago link generated for subscription {}, paymentId={}, preferenceId={}",
                subscription.getId(), finalPayment.getId(), mpResponse.id());

        return new PaymentLinkResult(
                mpResponse.init_point(),
                mpResponse.date_of_expiration() != null ? mpResponse.date_of_expiration() : expirationStr,
                externalReference
        );
    }
}
