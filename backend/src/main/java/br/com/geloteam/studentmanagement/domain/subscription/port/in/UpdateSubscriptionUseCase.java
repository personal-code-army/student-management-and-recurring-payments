package br.com.geloteam.studentmanagement.domain.subscription.port.in;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;

public interface UpdateSubscriptionUseCase {
    Subscription execute(Long id, Subscription subscription);
}
