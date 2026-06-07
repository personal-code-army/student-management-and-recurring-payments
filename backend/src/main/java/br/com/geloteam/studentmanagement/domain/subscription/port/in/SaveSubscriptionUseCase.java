package br.com.geloteam.studentmanagement.domain.subscription.port.in;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;

public interface SaveSubscriptionUseCase {
    Subscription execute(Subscription subscription);
}
