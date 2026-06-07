package br.com.geloteam.studentmanagement.application.subscription;

import br.com.geloteam.studentmanagement.application.payment.PaymentUseCaseImpl;
import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.out.SubscriptionRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.BadRequestException;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class SubscriptionUseCaseImplTest {

    @Mock
    private SubscriptionRepositoryPort subscriptionRepository;

    @Mock
    private StudentRepositoryPort studentRepository;

    @Mock
    private PaymentUseCaseImpl paymentUseCase;

    @InjectMocks
    private SubscriptionUseCaseImpl subscriptionUseCase;

    private Subscription buildSubscription() {
        Subscription sub = new Subscription();
        sub.setId(1L);
        sub.setStudentId(10L);
        sub.setPlanId(2L);
        sub.setStartDate(LocalDate.now());
        sub.setStatus("Ativo");
        sub.setPaymentMethod("Pix");
        return sub;
    }

    private Student buildStudent() {
        Student s = new Student();
        s.setId(10L);
        s.setName("João Silva");
        s.setCpf("12345678901");
        s.setActive(true);
        return s;
    }

    private Payment buildPayment(String status) {
        Payment p = new Payment();
        p.setId(1L);
        p.setSubscriptionId(1L);
        p.setStatus(status);
        p.setDueDate(LocalDate.now());
        return p;
    }

    // ─── findById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return subscription when ID exists")
    void findByIdShouldReturnSubscriptionWhenExists() {
        Subscription subscription = buildSubscription();
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscription));

        Subscription result = subscriptionUseCase.findById(1L);

        assertNotNull(result);
        assertEquals("Ativo", result.getStatus());
        assertEquals(10L, result.getStudentId());
        verify(subscriptionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when subscription ID does not exist")
    void findByIdShouldThrowNotFoundWhenMissing() {
        when(subscriptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subscriptionUseCase.findById(99L));
        verify(subscriptionRepository).findById(99L);
    }

    // ─── findAll ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return all subscriptions")
    void findAllShouldReturnList() {
        Subscription s1 = buildSubscription();
        Subscription s2 = new Subscription();
        s2.setId(2L);
        s2.setStudentId(20L);
        s2.setStatus("Inativo");

        when(subscriptionRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Subscription> result = subscriptionUseCase.findAll();

        assertEquals(2, result.size());
        assertEquals("Ativo", result.get(0).getStatus());
        assertEquals("Inativo", result.get(1).getStatus());
        verify(subscriptionRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no subscriptions exist")
    void findAllShouldReturnEmptyList() {
        when(subscriptionRepository.findAll()).thenReturn(List.of());

        List<Subscription> result = subscriptionUseCase.findAll();

        assertTrue(result.isEmpty());
    }

    // ─── findByStudentName ────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return subscriptions matching student name")
    void findByStudentNameShouldReturnMatching() {
        Subscription subscription = buildSubscription();
        when(subscriptionRepository.findAllByStudentName("João Silva")).thenReturn(List.of(subscription));

        List<Subscription> result = subscriptionUseCase.findByStudentName("João Silva");

        assertEquals(1, result.size());
        verify(subscriptionRepository).findAllByStudentName("João Silva");
    }

    @Test
    @DisplayName("Should return empty list when no subscription matches student name")
    void findByStudentNameShouldReturnEmptyWhenNoMatch() {
        when(subscriptionRepository.findAllByStudentName("Desconhecido")).thenReturn(List.of());

        List<Subscription> result = subscriptionUseCase.findByStudentName("Desconhecido");

        assertTrue(result.isEmpty());
    }

    // ─── save (execute Subscription) ─────────────────────────────────────────────

    @Test
    @DisplayName("Should create subscription and generate payment when student has no active subscription")
    void saveShouldSucceedAndGeneratePayment() {
        Subscription subscription = buildSubscription();
        Student student = buildStudent();

        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(subscriptionRepository.existsByStudentIdAndStatus(10L, "Ativo")).thenReturn(false);
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription result = subscriptionUseCase.execute(subscription);

        assertNotNull(result);
        assertEquals("Ativo", result.getStatus());
        verify(subscriptionRepository).save(subscription);
        verify(paymentUseCase).generatePaymentForSubscription(result);
    }

    @Test
    @DisplayName("Should throw NotFoundException when student does not exist on create")
    void saveShouldThrowNotFoundWhenStudentMissing() {
        Subscription subscription = buildSubscription();
        when(studentRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subscriptionUseCase.execute(subscription));
        verify(subscriptionRepository, never()).save(any());
        verify(paymentUseCase, never()).generatePaymentForSubscription(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when student already has an active subscription")
    void saveShouldThrowConflictWhenActiveSubscriptionExists() {
        Subscription subscription = buildSubscription();
        Student student = buildStudent();

        when(studentRepository.findById(10L)).thenReturn(Optional.of(student));
        when(subscriptionRepository.existsByStudentIdAndStatus(10L, "Ativo")).thenReturn(true);

        assertThrows(ConflictException.class, () -> subscriptionUseCase.execute(subscription));
        verify(subscriptionRepository, never()).save(any());
        verify(paymentUseCase, never()).generatePaymentForSubscription(any());
    }

    // ─── update (execute Long, Subscription) ─────────────────────────────────────

    @Test
    @DisplayName("Should update and renew subscription when status is Ativo and no pending payment")
    void updateShouldRenewWhenActiveAndNoPendingPayment() {
        Subscription existing = buildSubscription();
        Subscription incoming = buildSubscription();
        incoming.setStatus("Ativo");

        Payment paidPayment = buildPayment("Pago");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentUseCase.findBySubscription(1L)).thenReturn(List.of(paidPayment));

        Subscription result = subscriptionUseCase.execute(1L, incoming);

        assertNotNull(result);
        assertEquals("Ativo", result.getStatus());
        verify(paymentUseCase).generatePaymentForSubscription(result);
    }

    @Test
    @DisplayName("Should deactivate subscription without generating payment when status is Inativo")
    void updateShouldDeactivateWithoutGeneratingPayment() {
        Subscription existing = buildSubscription();
        Subscription incoming = buildSubscription();
        incoming.setStatus("Inativo");

        Payment paidPayment = buildPayment("Pago");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentUseCase.findBySubscription(1L)).thenReturn(List.of(paidPayment));

        Subscription result = subscriptionUseCase.execute(1L, incoming);

        assertNotNull(result);
        assertEquals("Inativo", result.getStatus());
        verify(paymentUseCase, never()).generatePaymentForSubscription(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when subscription has a pending payment on renewal")
    void updateShouldThrowBadRequestWhenPaymentIsPending() {
        Subscription existing = buildSubscription();
        Subscription incoming = buildSubscription();

        Payment pendingPayment = buildPayment("A receber");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentUseCase.findBySubscription(1L)).thenReturn(List.of(pendingPayment));

        assertThrows(BadRequestException.class, () -> subscriptionUseCase.execute(1L, incoming));
        verify(paymentUseCase, never()).generatePaymentForSubscription(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when subscription has an overdue payment on renewal")
    void updateShouldThrowBadRequestWhenPaymentIsOverdue() {
        Subscription existing = buildSubscription();
        Subscription incoming = buildSubscription();

        Payment overduePayment = buildPayment("Vencido");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentUseCase.findBySubscription(1L)).thenReturn(List.of(overduePayment));

        assertThrows(BadRequestException.class, () -> subscriptionUseCase.execute(1L, incoming));
        verify(paymentUseCase, never()).generatePaymentForSubscription(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent subscription")
    void updateShouldThrowNotFoundWhenMissing() {
        Subscription incoming = buildSubscription();
        when(subscriptionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> subscriptionUseCase.execute(99L, incoming));
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should renew subscription and generate payment when there are no previous payments")
    void updateShouldRenewWhenNoPaymentsExist() {
        Subscription existing = buildSubscription();
        Subscription incoming = buildSubscription();
        incoming.setStatus("Ativo");

        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentUseCase.findBySubscription(1L)).thenReturn(List.of());

        Subscription result = subscriptionUseCase.execute(1L, incoming);

        assertNotNull(result);
        verify(paymentUseCase).generatePaymentForSubscription(result);
    }

    // ─── delete (execute Long) ────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete subscription successfully when ID exists")
    void deleteShouldSucceed() {
        when(subscriptionRepository.existsById(1L)).thenReturn(true);

        subscriptionUseCase.execute(1L);

        verify(subscriptionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when deleting non-existent subscription")
    void deleteShouldThrowNotFoundWhenMissing() {
        when(subscriptionRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> subscriptionUseCase.execute(99L));
        verify(subscriptionRepository, never()).deleteById(any());
    }
}
