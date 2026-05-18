package br.com.geloteam.studentmanagement.infrastructure.persistence.subscription;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.out.SubscriptionRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepositoryAdapter implements SubscriptionRepositoryPort {

    private final SubscriptionJpaRepository jpa;

    public SubscriptionRepositoryAdapter(SubscriptionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Subscription> findById(Long id) { return jpa.findById(id); }
    @Override public List<Subscription> findAll() { return jpa.findAll(); }
    @Override public Subscription save(Subscription sub) { return jpa.save(sub); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
    @Override public boolean existsByStudentIdAndStatus(Long studentId, String status) { return jpa.existsByStudentIdAndStatus(studentId, status); }
    @Override public List<Subscription> findAllByStudentId(Long studentId) { return jpa.findAllByStudentId(studentId); }
    @Override public List<Subscription> findAllByStudentName(String name) { return jpa.findAllByStudentName(name); }
}
