package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Payment;
import br.com.geloteam.studentmanagement.Models.Student;
import br.com.geloteam.studentmanagement.Models.Subscription;
import br.com.geloteam.studentmanagement.Repositories.SubscriptionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return subscription.orElseThrow(() -> new ValidationException(
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
            throw new RuntimeException("O aluno possui pagamentos pendentes ou vencidos!");
        }

        //se for renovação da assinatura, o plano será ativo e irá gerar o pagamento
        //caso for cancelamento o plano será inativado e não irá gerar pagamento
        if (savedSubscription.getStatus().equals("Ativo")) {
            paymentService.savePaymentSubscription(savedSubscription);
        }

        return savedSubscription;
    }

    @Transactional
    public void delete(long id) {
        subscriptionRepository.deleteById(id);
    }

    @Transactional
    public Subscription save(Subscription subscription) {
        Student student = studentService.findById(subscription.getStudent().getId());
        if (!subscriptionRepository.existsByStudentIdAndStatus(student.getId(), "ATIVO")) {
            throw new RuntimeException("O aluno já possui uma assinatura ativa");
        }

        Subscription savedSubscription = this.subscriptionRepository.save(subscription);

        paymentService.savePaymentSubscription(savedSubscription);

        return savedSubscription;

    }

}
