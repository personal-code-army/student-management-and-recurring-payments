package br.com.geloteam.studentmanagement.infrastructure.persistence.plan;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlanRepositoryAdapter implements PlanRepositoryPort {

    private final PlanJpaRepository jpa;

    public PlanRepositoryAdapter(PlanJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Plan> findById(Long id) { return jpa.findById(id); }
    @Override public List<Plan> findAll() { return jpa.findAll(); }
    @Override public Plan save(Plan plan) { return jpa.save(plan); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
}
