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

    @Override public Optional<Plan> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Plan> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public Plan save(Plan plan) { return toDomain(jpa.save(toJpaEntity(plan))); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }

    private Plan toDomain(PlanJpaEntity e) {
        Plan p = new Plan();
        p.setId(e.getId());
        p.setName(e.getName());
        p.setMonthlyAmount(e.getMonthlyAmount());
        p.setFrequency(e.getFrequency());
        return p;
    }

    private PlanJpaEntity toJpaEntity(Plan p) {
        PlanJpaEntity e = new PlanJpaEntity();
        e.setId(p.getId());
        e.setName(p.getName());
        e.setMonthlyAmount(p.getMonthlyAmount());
        e.setFrequency(p.getFrequency());
        return e;
    }
}
