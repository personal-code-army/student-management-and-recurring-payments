package br.com.geloteam.studentmanagement.infrastructure.persistence.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpa;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Payment> findById(Long id) { return jpa.findById(id); }
    @Override public List<Payment> findAll() { return jpa.findAll(); }
    @Override public Payment save(Payment payment) { return jpa.save(payment); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
    @Override public List<Payment> findAllBySubscriptionId(Long subscriptionId) { return jpa.findAllBySubscriptionId(subscriptionId); }
    @Override public List<Payment> findAllBySubscriptionStudentName(String name) { return jpa.findAllBySubscriptionStudentName(name); }
}
