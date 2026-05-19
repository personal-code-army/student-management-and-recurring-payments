package br.com.geloteam.studentmanagement.infrastructure.persistence.plan;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanJpaRepository extends JpaRepository<Plan, Long> {
}
