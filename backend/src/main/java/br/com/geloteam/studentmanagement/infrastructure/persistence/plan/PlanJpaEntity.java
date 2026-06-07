package br.com.geloteam.studentmanagement.infrastructure.persistence.plan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plans")
@Getter @Setter @NoArgsConstructor
public class PlanJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "monthly_amount")
    private Double monthlyAmount;

    private Integer frequency;
}
