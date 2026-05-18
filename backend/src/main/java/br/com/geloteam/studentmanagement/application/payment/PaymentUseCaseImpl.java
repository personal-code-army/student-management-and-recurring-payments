package br.com.geloteam.studentmanagement.application.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.in.DeletePaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.FindPaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.SavePaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.UpdatePaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class PaymentUseCaseImpl implements SavePaymentUseCase, UpdatePaymentUseCase, DeletePaymentUseCase, FindPaymentUseCase {

    private final PaymentRepositoryPort paymentRepository;

    public PaymentUseCaseImpl(PaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pagamento não encontrado com ID: " + id));
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> findBySubscription(Long subscriptionId) {
        return paymentRepository.findAllBySubscriptionId(subscriptionId);
    }

    @Override
    public List<Payment> findByStudentName(String name) {
        return paymentRepository.findAllBySubscriptionStudentName(name);
    }

    @Override
    @Transactional
    public Payment execute(Payment payment) {
        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} saved", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public Payment execute(Long id, Payment incoming) {
        Payment payment = findById(id);
        payment.setDescription(incoming.getDescription());
        payment.setValue(incoming.getValue());
        payment.setPaymentMethod(incoming.getPaymentMethod());
        payment.setDueDate(incoming.getDueDate());
        payment.setIssueDate(incoming.getIssueDate());
        payment.setStatus(incoming.getStatus());
        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} updated", saved.getId());
        return saved;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new NotFoundException("O pagamento não existe ou já foi excluído!");
        }
        paymentRepository.deleteById(id);
        log.info("Payment {} deleted", id);
    }

    @Transactional
    public Payment generatePaymentForSubscription(Subscription subscription) {
        Payment payment = new Payment();
        payment.setDescription("Assinatura | " + subscription.getPlan().getName());
        payment.setValue(subscription.getPlan().getMonthlyAmount());
        payment.setPaymentMethod(subscription.getPaymentMethod());
        payment.setDueDate(LocalDate.now());
        payment.setIssueDate(null);
        payment.setStatus("A receber");
        payment.setSubscription(subscription);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment generated for subscription {}, due: {}", subscription.getId(), saved.getDueDate());
        return saved;
    }
}
