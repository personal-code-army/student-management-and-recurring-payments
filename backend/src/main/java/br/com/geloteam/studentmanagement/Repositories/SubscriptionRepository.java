package br.com.geloteam.studentmanagement.Repositories;

import br.com.geloteam.studentmanagement.Models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByStudentName(String name);

    List<Subscription> findAllByStudentId(Long studentId);

    boolean existsByStudentIdAndStatus(Long studentId, String status);

}
