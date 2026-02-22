package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Payment;
import br.com.geloteam.studentmanagement.Repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    public Payment findById(Long id){
        Optional<Payment> payment = this.paymentRepository.findById(id);
        return payment.orElseThrow(() -> new ValidationException(
                "Pagamento não encontrado"
        ));
    }

    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> findAllUserPayment(String name) {
        return paymentRepository.findAllBySubscriptionStudentName(name);
    }

    @Transactional
    public Payment update(Payment payments){
        Payment payment = findById(payments.getId());
        return this.paymentRepository.save(payments);
    }

    @Transactional
    public void delete(long id){
        paymentRepository.deleteById(id);
    }

    @Transactional
    public Payment save(Payment payment){
        return this.paymentRepository.save(payment);
    }

}
