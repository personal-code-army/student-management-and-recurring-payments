package br.com.geloteam.studentmanagement.domain.subscription.port.in;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;

import java.util.List;

public interface FindSubscriptionUseCase {
    Subscription findById(Long id);
    List<Subscription> findAll();
    List<Subscription> findByStudentName(String name);
}
