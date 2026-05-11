package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Payment;
import br.com.geloteam.studentmanagement.Models.Subscription;
import br.com.geloteam.studentmanagement.Repositories.PaymentRepository;
import br.com.geloteam.studentmanagement.exception.EntityIdNotExistsOrDelete;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    public Payment findById(Long id) {
        Optional<Payment> payment = this.paymentRepository.findById(id);
        return payment.orElseThrow(() -> new EntityNotFound(
                "Pagamento não encontrado"
        ));
    }

    public List<Payment> findAllPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> findAllPaymentsSubscription(Long id) {
        return paymentRepository.findAllBySubscriptionId(id);
    }

    public List<Payment> findAllUserPayment(String name) {
        return paymentRepository.findAllBySubscriptionStudentName(name);
    }

    // verificar se está realmente editando os dados
    @Transactional
    public Payment update(Payment payments) {
        Payment payment = findById(payments.getId());
        Payment saved = this.paymentRepository.save(payment);
        log.info("Payment {} updated", saved.getId());
        return saved;
    }

    @Transactional
    public void delete(long id) {
        if(!paymentRepository.existsById(id)){
            throw new EntityIdNotExistsOrDelete("O pagamento não existe ou já foi excluso!");
        }
        paymentRepository.deleteById(id);
        log.info("Payment {} deleted", id);
    }

    @Transactional
    public Payment save(Payment payment) {
        Payment saved = this.paymentRepository.save(payment);
        log.info("Payment {} saved", saved.getId());
        return saved;
    }

    // função para geração de pagamentos recorrentes
    @Transactional
    public List<Payment> saveRecurringPayment(Subscription subscription) {
        int frequency = subscription.getPlan().getFrequency();
        List<Payment> payments = new ArrayList<>();

        //cria vários pagamentos de acordo com a periodicidade do plano
        //ex: se for um plano trimestral, gera os 3 pagamentos
        if (frequency != 0) {
            for (int i = 0; i < frequency; i++) {
                Payment payment = new Payment();
                payment.setDescription("Assinatura | "
                        + subscription.getPlan().getName() + " | "
                        + (i + 1) + "/" + frequency);
                payment.setValue(subscription.getPlan().getMonthlyAmount());
                payment.setPaymentMethod(subscription.getPaymentMethod());
                if (i != 0) {
                    payment.setDueDate(subscription.getStartDate().plusMonths(i));
                } else {
                    payment.setDueDate(subscription.getStartDate());
                }
                payment.setIssueDate(null);
                payment.setStatus("A receber");
                payment.setSubscription(subscription);

                payments.add(this.paymentRepository.save(payment));
            }
        }

        log.info("{} recurring payments generated for subscription {}", payments.size(), subscription.getId());
        return payments;

    }

//    função para geração de pagamentos a partir da assinatura do aluno |
//    Modelo de geração de pagamentos específico do Gelo Team
    @Transactional
    public Payment savePaymentSubscription(Subscription subscription) {
        Payment payment = new Payment();
        payment.setDescription("Assinatura | "
                + subscription.getPlan().getName());
        payment.setValue(subscription.getPlan().getMonthlyAmount());
        payment.setPaymentMethod(subscription.getPaymentMethod());
        payment.setDueDate(LocalDate.now());
        payment.setIssueDate(null);
        payment.setStatus("A receber");
        payment.setSubscription(subscription);

        Payment saved = this.paymentRepository.save(payment);
        log.info("Initial payment created for subscription {}, due: {}", subscription.getId(), saved.getDueDate());
        return saved;
    }
}
