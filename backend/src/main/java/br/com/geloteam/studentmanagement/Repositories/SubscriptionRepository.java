package br.com.geloteam.studentmanagement.Repositories;

import br.com.geloteam.studentmanagement.Models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
