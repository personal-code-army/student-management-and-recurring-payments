package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Payment;
import br.com.geloteam.studentmanagement.Models.Subscription;
import br.com.geloteam.studentmanagement.Repositories.PaymentRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    public Payment findById(Long id) {
        Optional<Payment> payment = this.paymentRepository.findById(id);
        return payment.orElseThrow(() -> new ValidationException(
                "Pagamento não encontrado"
        ));
    }

    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> findAllPaymentsSubscription(Long id){
        return paymentRepository.findAllBySubscriptionId(id);
    }

    public List<Payment> findAllUserPayment(String name) {
        return paymentRepository.findAllBySubscriptionStudentName(name);
    }

    @Transactional
    public Payment update(Payment payments) {
        Payment payment = findById(payments.getId());
        return this.paymentRepository.save(payment);
    }

    @Transactional
    public void delete(long id) {
        paymentRepository.deleteById(id);
    }

    @Transactional
    public Payment save(Payment payment) {
        return this.paymentRepository.save(payment);
    }

    @Transactional
    public List<Payment> savePaymentSubscription(Subscription subscription) {
        int frequency = subscription.getPlan().getFrequency();
        List<Payment> payments = new ArrayList<>();

        //cria vários pagamentos de acordo com a periodicidade do plano
        //ex: se for um plano trimestral, gera os 3 pagamentos
        if (frequency != 0) {
            for (int i = 0; i < frequency; i++) {
                Payment payment = new Payment();
                payment.setDescription("Assinatura | "
                        + subscription.getPlan() + " | "
                        + (i+1) + "/" + frequency);
                payment.setValue(subscription.getPlan().getMonthly_amount());
                payment.setPaymentMethod(subscription.getPaymentMethod());
                if (i != 0) {
                    payment.setDueDate(subscription.getDueDate().plusMonths(i));
                } else {
                    payment.setDueDate(subscription.getDueDate());
                }
                payment.setPaymentDate(null);
                payment.setStatus("A receber");
                payment.setSubscription(subscription);

                payments.add(this.paymentRepository.save(payment));
            }
        }

        return payments;

    }
}
