package br.com.geloteam.studentmanagement.infrastructure.persistence.subscription;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.out.SubscriptionRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.persistence.plan.PlanJpaEntity;
import br.com.geloteam.studentmanagement.infrastructure.persistence.student.StudentJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryAdapter implements SubscriptionRepositoryPort {

    private final SubscriptionJpaRepository jpa;

    public SubscriptionRepositoryAdapter(SubscriptionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Subscription> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Subscription> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public Subscription save(Subscription sub) { return toDomain(jpa.save(toJpaEntity(sub))); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
    @Override public boolean existsByStudentIdAndStatus(Long studentId, String status) { return jpa.existsByStudentIdAndStatus(studentId, status); }
    @Override public List<Subscription> findAllByStudentId(Long studentId) { return jpa.findAllByStudentId(studentId).stream().map(this::toDomain).toList(); }
    @Override public List<Subscription> findAllByStudentName(String name) { return jpa.findAllByStudentName(name).stream().map(this::toDomain).toList(); }

    private Subscription toDomain(SubscriptionJpaEntity e) {
        Subscription s = new Subscription();
        s.setId(e.getId());
        s.setStudentId(e.getStudent().getId());
        s.setPlanId(e.getPlan().getId());
        s.setStartDate(e.getStartDate());
        s.setStatus(e.getStatus());
        s.setPaymentMethod(e.getPaymentMethod());
        return s;
    }

    private SubscriptionJpaEntity toJpaEntity(Subscription s) {
        SubscriptionJpaEntity e = new SubscriptionJpaEntity();
        e.setId(s.getId());
        StudentJpaEntity student = new StudentJpaEntity();
        student.setId(s.getStudentId());
        e.setStudent(student);
        PlanJpaEntity plan = new PlanJpaEntity();
        plan.setId(s.getPlanId());
        e.setPlan(plan);
        e.setStartDate(s.getStartDate());
        e.setStatus(s.getStatus());
        e.setPaymentMethod(s.getPaymentMethod());
        return e;
    }
}
