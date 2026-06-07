package br.com.geloteam.studentmanagement.domain.payment.port.in;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;

import java.util.List;

public interface FindPaymentUseCase {
    Payment findById(Long id);
    List<Payment> findAll();
    List<Payment> findBySubscription(Long subscriptionId);
    List<Payment> findByStudentName(String name);
}
