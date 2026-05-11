package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Payment;
import br.com.geloteam.studentmanagement.Models.Student;
import br.com.geloteam.studentmanagement.Models.Subscription;
import br.com.geloteam.studentmanagement.Repositories.SubscriptionRepository;
import br.com.geloteam.studentmanagement.exception.SubscriptionAlreadyExists;
import br.com.geloteam.studentmanagement.exception.EntityIdNotExistsOrDelete;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import br.com.geloteam.studentmanagement.exception.SubscriptionPendingPayament;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    PaymentService paymentService;

    @Autowired
    StudentService studentService;

    public Subscription findById(Long id) {
        Optional<Subscription> subscription = this.subscriptionRepository.findById(id);
        return subscription.orElseThrow(() -> new EntityNotFound(
                "Assinatura não encontrado"
        ));
    }

    public List<Subscription> findAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public List<Subscription> findAllUserSubscription(String name) {
        return subscriptionRepository.findAllByStudentName(name);
    }

    @Transactional
    public Subscription update(Subscription subscriptions) {
        Subscription subscription = findById(subscriptions.getId());

        subscription.setStartDate(subscriptions.getStartDate());
        subscription.setStatus(subscriptions.getStatus());
        subscription.setPaymentMethod(subscriptions.getPaymentMethod());
        subscription.setPlan(subscriptions.getPlan());

        Subscription savedSubscription = this.subscriptionRepository.save(subscription);

        Payment lastPayment = paymentService.findAllPaymentsSubscription(savedSubscription.getId()).getLast();

        if (lastPayment.getStatus().equals("A receber") || lastPayment.getStatus().equals("Vencido")) {
            log.warn("Subscription {} renewal denied: pending or overdue payment exists", savedSubscription.getId());
            throw new SubscriptionPendingPayament("O aluno possui pagamentos pendentes ou vencidos!");
        }

        if (savedSubscription.getStatus().equals("Ativo")) {
            paymentService.savePaymentSubscription(savedSubscription);
            log.info("Subscription {} renewed, new payment generated", savedSubscription.getId());
        } else {
            log.info("Subscription {} deactivated", savedSubscription.getId());
        }

        return savedSubscription;
    }

    @Transactional
    public void delete(long id) {
        if(!subscriptionRepository.existsById(id)){
            throw new EntityIdNotExistsOrDelete("A subscrição não existe, ou já foi excluida.");
        }
        subscriptionRepository.deleteById(id);
        log.info("Subscription {} deleted", id);
    }

    @Transactional
    public Subscription save(Subscription subscription) {
        Student student = studentService.findById(subscription.getStudent().getId());
        if (subscriptionRepository.existsByStudentIdAndStatus(student.getId(), "ATIVO")) {
            log.warn("Subscription not created: student {} already has an active subscription", student.getId());
            throw new SubscriptionAlreadyExists("O aluno já possui uma assinatura ativa");
        }

        Subscription savedSubscription = this.subscriptionRepository.save(subscription);

        paymentService.savePaymentSubscription(savedSubscription);
        log.info("Subscription created for student {}, plan {}", student.getId(), savedSubscription.getPlan().getId());

        return savedSubscription;
    }

}
