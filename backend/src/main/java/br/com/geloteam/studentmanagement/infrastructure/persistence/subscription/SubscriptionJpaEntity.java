package br.com.geloteam.studentmanagement.infrastructure.persistence.subscription;

import br.com.geloteam.studentmanagement.infrastructure.persistence.plan.PlanJpaEntity;
import br.com.geloteam.studentmanagement.infrastructure.persistence.student.StudentJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Getter @Setter @NoArgsConstructor
public class SubscriptionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentJpaEntity student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private PlanJpaEntity plan;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    private String status;

    @Column(name = "payment_method")
    private String paymentMethod;
}
