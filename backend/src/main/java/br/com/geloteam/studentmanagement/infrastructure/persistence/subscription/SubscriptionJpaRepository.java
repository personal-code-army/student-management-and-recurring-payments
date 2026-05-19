package br.com.geloteam.studentmanagement.infrastructure.persistence.subscription;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionJpaRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByStudentName(String name);

    List<Subscription> findAllByStudentId(Long studentId);

    boolean existsByStudentIdAndStatus(Long studentId, String status);
}
