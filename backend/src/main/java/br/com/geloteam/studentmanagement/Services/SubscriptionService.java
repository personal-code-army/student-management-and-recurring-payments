package br.com.geloteam.studentmanagement.Services;

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

    //@Autowired
    //StudentService studentService;

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
        return subscriptionRepository.findAllBySubscriptionStudentName(name);
    }

    @Transactional
    public Subscription update(Subscription subscriptions) {
        Subscription subscription = findById(subscriptions.getId());
        return this.subscriptionRepository.save(subscription);
    }

    @Transactional
    public void delete(long id) {
        subscriptionRepository.deleteById(id);
    }

    @Transactional
    public Subscription save(Subscription subscription) {
        //Student student = studentService.findById(subscription.getStudent().getId());
        //if (studentService.getAllSubscription(student).size() >= 1
        //        && studentService.getSubscription = "Ativo") {
        //    throw new RuntimeException("O aluno já possui uma assinatura ativa");
        //}

        paymentService.savePaymentSubscription(subscription);

        return this.subscriptionRepository.save(subscription);


    }


}
