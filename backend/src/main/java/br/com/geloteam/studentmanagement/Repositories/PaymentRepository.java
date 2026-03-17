package br.com.geloteam.studentmanagement.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.geloteam.studentmanagement.Models.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

//    List<Payment> findAllBySubscriptionStudentName(String name);

    List<Payment> findAllBySubscriptionId(Long id);

}
