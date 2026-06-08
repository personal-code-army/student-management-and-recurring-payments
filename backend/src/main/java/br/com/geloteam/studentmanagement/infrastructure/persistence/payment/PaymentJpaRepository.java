package br.com.geloteam.studentmanagement.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentJpaRepository extends JpaRepository<PaymentJpaEntity, Long> {
    List<PaymentJpaEntity> findAllBySubscriptionStudentName(String name);
    List<PaymentJpaEntity> findAllBySubscriptionId(Long id);

    @Modifying
    @Query("UPDATE PaymentJpaEntity p SET p.status = 'Vencido' " +
           "WHERE p.status IN ('A receber', 'pending', 'PENDING') " +
           "AND p.dueDate < :today")
    int markOverdue(@Param("today") LocalDate today);
}
