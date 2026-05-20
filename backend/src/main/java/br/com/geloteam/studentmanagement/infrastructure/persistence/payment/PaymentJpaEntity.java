package br.com.geloteam.studentmanagement.infrastructure.persistence.payment;

import br.com.geloteam.studentmanagement.infrastructure.persistence.subscription.SubscriptionJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor
public class PaymentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subscription_id", nullable = false)
    private SubscriptionJpaEntity subscription;

    private String description;
    private double value;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    private String status;

    @Column(name = "mercado_pago_preference_id")
    private String mercadoPagoPreferenceId;

    @Column(name = "mercado_pago_payment_id")
    private String mercadoPagoPaymentId;

    @Column(name = "checkout_url", columnDefinition = "TEXT")
    private String checkoutUrl;

    @Column(name = "external_reference", unique = true)
    private String externalReference;

    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "payer_email")
    private String payerEmail;
}
