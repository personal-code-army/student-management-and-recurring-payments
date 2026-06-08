package br.com.geloteam.studentmanagement.infrastructure.scheduling;

import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class OverduePaymentScheduler {

    private final PaymentRepositoryPort paymentRepository;

    public OverduePaymentScheduler(PaymentRepositoryPort paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void markOverduePayments() {
        int updated = paymentRepository.markOverdue(LocalDate.now());
        if (updated > 0) {
            log.info("Marcados {} pagamento(s) como Vencido", updated);
        }
    }
}
