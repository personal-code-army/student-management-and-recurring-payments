package br.com.geloteam.studentmanagement.infrastructure.persistence.student;

import br.com.geloteam.studentmanagement.infrastructure.persistence.plan.PlanJpaEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Getter @Setter @NoArgsConstructor
public class StudentJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String phone;
    private String email;
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id")
    private PlanJpaEntity plan;

    private boolean active;
}
