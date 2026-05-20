package br.com.geloteam.studentmanagement.application.subscription;

import br.com.geloteam.studentmanagement.application.payment.PaymentUseCaseImpl;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.DeleteSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.FindSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.SaveSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.UpdateSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.out.SubscriptionRepositoryPort;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.BadRequestException;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SubscriptionUseCaseImpl implements SaveSubscriptionUseCase, UpdateSubscriptionUseCase,
        DeleteSubscriptionUseCase, FindSubscriptionUseCase {

    private final SubscriptionRepositoryPort subscriptionRepository;
    private final StudentRepositoryPort studentRepository;
    private final PaymentUseCaseImpl paymentUseCase;

    public SubscriptionUseCaseImpl(SubscriptionRepositoryPort subscriptionRepository,
                                   StudentRepositoryPort studentRepository,
                                   PaymentUseCaseImpl paymentUseCase) {
        this.subscriptionRepository = subscriptionRepository;
        this.studentRepository = studentRepository;
        this.paymentUseCase = paymentUseCase;
    }

    @Override
    public Subscription findById(Long id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada com ID: " + id));
    }

    @Override
    public List<Subscription> findAll() {
        return subscriptionRepository.findAll();
    }

    @Override
    public List<Subscription> findByStudentName(String name) {
        return subscriptionRepository.findAllByStudentName(name);
    }

    @Override
    @Transactional
    public Subscription execute(Subscription subscription) {
        var student = studentRepository.findById(subscription.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado com ID: " + subscription.getStudentId()));

        if (subscriptionRepository.existsByStudentIdAndStatus(student.getId(), "Ativo")) {
            log.warn("Subscription not created: student {} already has an active subscription", student.getId());
            throw new ConflictException("SUBSCRIPTION_ALREADY_EXISTS", "O aluno já possui uma assinatura ativa");
        }

        Subscription saved = subscriptionRepository.save(subscription);
        paymentUseCase.generatePaymentForSubscription(saved);
        log.info("Subscription created for student {}, plan {}", student.getId(), saved.getPlanId());
        return saved;
    }

    @Override
    @Transactional
    public Subscription execute(Long id, Subscription incoming) {
        Subscription subscription = findById(id);

        subscription.setStartDate(incoming.getStartDate());
        subscription.setStatus(incoming.getStatus());
        subscription.setPaymentMethod(incoming.getPaymentMethod());
        subscription.setPlanId(incoming.getPlanId());

        Subscription saved = subscriptionRepository.save(subscription);

        var payments = paymentUseCase.findBySubscription(saved.getId());
        if (!payments.isEmpty()) {
            var lastPayment = payments.getLast();
            if (lastPayment.getStatus().equals("A receber") || lastPayment.getStatus().equals("Vencido")) {
                log.warn("Subscription {} renewal denied: pending or overdue payment exists", saved.getId());
                throw new BadRequestException("SUBSCRIPTION_PENDING_PAYMENT", "O aluno possui pagamentos pendentes ou vencidos!");
            }
        }

        if (saved.getStatus().equals("Ativo")) {
            paymentUseCase.generatePaymentForSubscription(saved);
            log.info("Subscription {} renewed, new payment generated", saved.getId());
        } else {
            log.info("Subscription {} deactivated", saved.getId());
        }

        return saved;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new NotFoundException("A assinatura não existe ou já foi excluída!");
        }
        subscriptionRepository.deleteById(id);
        log.info("Subscription {} deleted", id);
    }
}
