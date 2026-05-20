package br.com.geloteam.studentmanagement.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    List<PaymentJpaEntity> findAllBySubscriptionStudentName(String name);
    List<PaymentJpaEntity> findAllBySubscriptionId(Long id);
    Optional<PaymentJpaEntity> findByExternalReference(String externalReference);
}
