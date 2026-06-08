package br.com.geloteam.studentmanagement.infrastructure.scheduling;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class OverduePaymentScheduler {

    private final PaymentRepositoryPort paymentRepository;

    public OverduePaymentScheduler(PaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOverduePayments() {
        List<Payment> overdue = paymentRepository.findAllPendingOverdue(LocalDate.now());
        if (overdue.isEmpty()) return;

        for (Payment payment : overdue) {
            payment.setStatus("Vencido");
            paymentRepository.save(payment);
        }
        log.info("Marcados {} pagamento(s) como Vencido", overdue.size());
    }
}
