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
}
