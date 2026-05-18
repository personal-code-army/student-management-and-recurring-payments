package br.com.geloteam.studentmanagement.infrastructure.persistence.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllBySubscriptionStudentName(String name);

    List<Payment> findAllBySubscriptionId(Long id);
}
