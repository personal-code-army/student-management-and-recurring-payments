package br.com.geloteam.studentmanagement.infrastructure.persistence.subscription;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubscriptionJpaRepository extends JpaRepository<SubscriptionJpaEntity, Long> {
    List<SubscriptionJpaEntity> findAllByStudentName(String name);
    List<SubscriptionJpaEntity> findAllByStudentId(Long studentId);
    boolean existsByStudentIdAndStatus(Long studentId, String status);
}
