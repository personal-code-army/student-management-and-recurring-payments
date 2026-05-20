package br.com.geloteam.studentmanagement.infrastructure.persistence.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.persistence.subscription.SubscriptionJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository jpa;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Payment> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Payment> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public Payment save(Payment payment) { return toDomain(jpa.save(toJpaEntity(payment))); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
    @Override public List<Payment> findAllBySubscriptionId(Long subscriptionId) { return jpa.findAllBySubscriptionId(subscriptionId).stream().map(this::toDomain).toList(); }
    @Override public List<Payment> findAllBySubscriptionStudentName(String name) { return jpa.findAllBySubscriptionStudentName(name).stream().map(this::toDomain).toList(); }
    @Override public Optional<Payment> findByExternalReference(String externalReference) { return jpa.findByExternalReference(externalReference).map(this::toDomain); }

    private Payment toDomain(PaymentJpaEntity e) {
        Payment p = new Payment();
        p.setId(e.getId());
        p.setSubscriptionId(e.getSubscription().getId());
        p.setDescription(e.getDescription());
        p.setValue(e.getValue());
        p.setPaymentMethod(e.getPaymentMethod());
        p.setDueDate(e.getDueDate());
        p.setIssueDate(e.getIssueDate());
        p.setStatus(e.getStatus());
        p.setMercadoPagoPreferenceId(e.getMercadoPagoPreferenceId());
        p.setMercadoPagoPaymentId(e.getMercadoPagoPaymentId());
        p.setCheckoutUrl(e.getCheckoutUrl());
        p.setExternalReference(e.getExternalReference());
        p.setPayerName(e.getPayerName());
        p.setPayerEmail(e.getPayerEmail());
        return p;
    }

    private PaymentJpaEntity toJpaEntity(Payment p) {
        PaymentJpaEntity e = new PaymentJpaEntity();
        e.setId(p.getId());
        if (p.getSubscriptionId() != null) {
            SubscriptionJpaEntity sub = new SubscriptionJpaEntity();
            sub.setId(p.getSubscriptionId());
            e.setSubscription(sub);
        }
        e.setDescription(p.getDescription());
        e.setValue(p.getValue());
        e.setPaymentMethod(p.getPaymentMethod());
        e.setDueDate(p.getDueDate());
        e.setIssueDate(p.getIssueDate());
        e.setStatus(p.getStatus());
        e.setMercadoPagoPreferenceId(p.getMercadoPagoPreferenceId());
        e.setMercadoPagoPaymentId(p.getMercadoPagoPaymentId());
        e.setCheckoutUrl(p.getCheckoutUrl());
        e.setExternalReference(p.getExternalReference());
        e.setPayerName(p.getPayerName());
        e.setPayerEmail(p.getPayerEmail());
        return e;
    }
}
