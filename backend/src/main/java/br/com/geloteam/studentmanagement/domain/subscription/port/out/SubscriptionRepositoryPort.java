package br.com.geloteam.studentmanagement.domain.subscription.port.out;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepositoryPort {
    Optional<Subscription> findById(Long id);
    List<Subscription> findAll();
    Subscription save(Subscription subscription);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByStudentIdAndStatus(Long studentId, String status);
    List<Subscription> findAllByStudentId(Long studentId);
    List<Subscription> findAllByStudentName(String name);
}
