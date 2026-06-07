package br.com.geloteam.studentmanagement.domain.payment.port.in;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;

public interface UpdatePaymentUseCase {
    Payment execute(Long id, Payment payment);
}
