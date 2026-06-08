package br.com.geloteam.studentmanagement.application.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import br.com.geloteam.studentmanagement.domain.payment.port.out.PaymentRepositoryPort;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentUseCaseImplTest {

    @Mock
    private PaymentRepositoryPort paymentRepository;

    @Mock
    private PlanRepositoryPort planRepository;

    @InjectMocks
    private PaymentUseCaseImpl paymentUseCase;

    private Payment buildPayment() {
        Payment p = new Payment();
        p.setId(1L);
        p.setSubscriptionId(10L);
        p.setDescription("Assinatura | Mensal");
        p.setValue(99.90);
        p.setPaymentMethod("Pix");
        p.setDueDate(LocalDate.now());
        p.setIssueDate(null);
        p.setStatus("A receber");
        return p;
    }

    private Plan buildPlan() {
        Plan plan = new Plan();
        plan.setId(2L);
        plan.setName("Mensal");
        plan.setMonthlyAmount(99.90);
        plan.setFrequency(1);
        return plan;
    }

    private Subscription buildSubscription() {
        Subscription sub = new Subscription();
        sub.setId(10L);
        sub.setStudentId(1L);
        sub.setPlanId(2L);
        sub.setStartDate(LocalDate.now());
        sub.setStatus("Ativo");
        sub.setPaymentMethod("Pix");
        return sub;
    }

    // ─── findById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return payment when ID exists")
    void findByIdShouldReturnPaymentWhenExists() {
        Payment payment = buildPayment();
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        Payment result = paymentUseCase.findById(1L);

        assertNotNull(result);
        assertEquals("Assinatura | Mensal", result.getDescription());
        assertEquals("A receber", result.getStatus());
        verify(paymentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when payment ID does not exist")
    void findByIdShouldThrowNotFoundWhenMissing() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentUseCase.findById(99L));
        verify(paymentRepository).findById(99L);
    }

    // ─── findAll ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all payments")
    void findAllShouldReturnList() {
        Payment p1 = buildPayment();
        Payment p2 = new Payment();
        p2.setId(2L);
        p2.setDescription("Assinatura | Trimestral");
        p2.setStatus("Pago");

        when(paymentRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Payment> result = paymentUseCase.findAll();

        assertEquals(2, result.size());
        assertEquals("Assinatura | Mensal", result.get(0).getDescription());
        assertEquals("Assinatura | Trimestral", result.get(1).getDescription());
        verify(paymentRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no payments exist")
    void findAllShouldReturnEmptyList() {
        when(paymentRepository.findAll()).thenReturn(List.of());

        List<Payment> result = paymentUseCase.findAll();

        assertTrue(result.isEmpty());
    }

    // ─── findBySubscription ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return payments for a given subscription")
    void findBySubscriptionShouldReturnPayments() {
        Payment payment = buildPayment();
        when(paymentRepository.findAllBySubscriptionId(10L)).thenReturn(List.of(payment));

        List<Payment> result = paymentUseCase.findBySubscription(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.getFirst().getSubscriptionId());
        verify(paymentRepository).findAllBySubscriptionId(10L);
    }

    @Test
    @DisplayName("Should return empty list when subscription has no payments")
    void findBySubscriptionShouldReturnEmptyWhenNone() {
        when(paymentRepository.findAllBySubscriptionId(99L)).thenReturn(List.of());

        List<Payment> result = paymentUseCase.findBySubscription(99L);

        assertTrue(result.isEmpty());
    }

    // ─── findByStudentName ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return payments matching student name")
    void findByStudentNameShouldReturnMatching() {
        Payment payment = buildPayment();
        when(paymentRepository.findAllBySubscriptionStudentName("João Silva")).thenReturn(List.of(payment));

        List<Payment> result = paymentUseCase.findByStudentName("João Silva");

        assertEquals(1, result.size());
        verify(paymentRepository).findAllBySubscriptionStudentName("João Silva");
    }

    @Test
    @DisplayName("Should return empty list when no payment matches student name")
    void findByStudentNameShouldReturnEmptyWhenNoMatch() {
        when(paymentRepository.findAllBySubscriptionStudentName("Desconhecido")).thenReturn(List.of());

        List<Payment> result = paymentUseCase.findByStudentName("Desconhecido");

        assertTrue(result.isEmpty());
    }

    // ─── save (execute Payment) ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should save and return payment successfully")
    void saveShouldPersistAndReturnPayment() {
        Payment payment = buildPayment();
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentUseCase.execute(payment);

        assertNotNull(result);
        assertEquals("Assinatura | Mensal", result.getDescription());
        verify(paymentRepository).save(payment);
    }

    // ─── update (execute Long, Payment) ──────────────────────────────────────────

    @Test
    @DisplayName("Should update payment successfully")
    void updateShouldSucceed() {
        Payment existing = buildPayment();
        Payment incoming = new Payment();
        incoming.setDescription("Assinatura | Trimestral");
        incoming.setValue(249.90);
        incoming.setPaymentMethod("Cartão");
        incoming.setDueDate(LocalDate.now().plusDays(30));
        incoming.setIssueDate(LocalDate.now());
        incoming.setStatus("Pago");

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentUseCase.execute(1L, incoming);

        assertNotNull(result);
        assertEquals("Assinatura | Trimestral", result.getDescription());
        assertEquals(249.90, result.getValue());
        assertEquals("Pago", result.getStatus());
        verify(paymentRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent payment")
    void updateShouldThrowNotFoundWhenMissing() {
        Payment incoming = buildPayment();
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentUseCase.execute(99L, incoming));
        verify(paymentRepository, never()).save(any());
    }

    // ─── delete (execute Long) ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete payment successfully when ID exists")
    void deleteShouldSucceed() {
        when(paymentRepository.existsById(1L)).thenReturn(true);

        paymentUseCase.execute(1L);

        verify(paymentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent payment")
    void deleteShouldThrowNotFoundWhenMissing() {
        when(paymentRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> paymentUseCase.execute(99L));
        verify(paymentRepository, never()).deleteById(any());
    }

    // ─── generatePaymentForSubscription ──────────────────────────────────────────

    @Test
    @DisplayName("Should generate payment for subscription using plan data")
    void generatePaymentShouldUsesPlanDataCorrectly() {
        Subscription subscription = buildSubscription();
        Plan plan = buildPlan();

        when(planRepository.findById(2L)).thenReturn(Optional.of(plan));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment result = paymentUseCase.generatePaymentForSubscription(subscription);

        assertNotNull(result);
        assertEquals("Assinatura | Mensal", result.getDescription());
        assertEquals(99.90, result.getValue());
        assertEquals("Pix", result.getPaymentMethod());
        assertEquals("A receber", result.getStatus());
        assertEquals(LocalDate.now(), result.getIssueDate());
        assertEquals(10L, result.getSubscriptionId());
        verify(planRepository).findById(2L);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw NotFoundException when plan is not found during payment generation")
    void generatePaymentShouldThrowNotFoundWhenPlanMissing() {
        Subscription subscription = buildSubscription();
        when(planRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> paymentUseCase.generatePaymentForSubscription(subscription));
        verify(paymentRepository, never()).save(any());
    }
}
