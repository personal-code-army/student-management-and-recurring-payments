package br.com.geloteam.studentmanagement.domain.payment.port.out;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepositoryPort {
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
    Payment save(Payment payment);
    void deleteById(Long id);
    boolean existsById(Long id);
    List<Payment> findAllBySubscriptionId(Long subscriptionId);
    List<Payment> findAllBySubscriptionStudentName(String name);
    List<Payment> findAllPendingOverdue(LocalDate today);
}
