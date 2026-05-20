package br.com.geloteam.studentmanagement.infrastructure.persistence.plan;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanJpaRepository extends JpaRepository<PlanJpaEntity, Long> {
}
