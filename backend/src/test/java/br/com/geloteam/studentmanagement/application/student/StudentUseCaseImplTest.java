package br.com.geloteam.studentmanagement.application.student;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.out.SubscriptionRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.web.student.dto.StudentResponse;
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
class StudentUseCaseImplTest {

    @Mock
    private StudentRepositoryPort studentRepository;

    @Mock
    private SubscriptionRepositoryPort subscriptionRepository;

    @Mock
    private PlanRepositoryPort planRepository;

    @InjectMocks
    private FindStudentUseCaseImpl findStudentUseCase;

    @InjectMocks
    private CreateStudentUseCaseImpl createStudentUseCase;

    @InjectMocks
    private UpdateStudentUseCaseImpl updateStudentUseCase;

    @InjectMocks
    private DeleteStudentUseCaseImpl deleteStudentUseCase;

    private Student buildStudent() {
        Student s = new Student();
        s.setId(1L);
        s.setName("João Silva");
        s.setCpf("12345678901");
        s.setBirthDate(LocalDate.of(1995, 5, 20));
        s.setPhone("11999999999");
        s.setEmail("joao@email.com");
        s.setAddress("Rua das Flores, 123");
        s.setActive(true);
        return s;
    }

    @Test
    @DisplayName("Should return student when ID exists")
    void findByIdShouldReturnStudentWhenExists() {
        Student student = buildStudent();
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student result = findStudentUseCase.findById(1L);

        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        assertEquals("12345678901", result.getCpf());
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when ID does not exist")
    void findByIdShouldThrowNotFoundWhenMissing() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> findStudentUseCase.findById(99L));
        verify(studentRepository).findById(99L);
    }

    @Test
    @DisplayName("Should return all students")
    void findAllShouldReturnList() {
        Student s1 = buildStudent();
        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("Maria Souza");
        s2.setCpf("98765432100");
        s2.setBirthDate(LocalDate.of(2000, 3, 10));

        when(studentRepository.findAll()).thenReturn(List.of(s1, s2));

        List<Student> result = findStudentUseCase.findAll();

        assertEquals(2, result.size());
        assertEquals("João Silva", result.get(0).getName());
        assertEquals("Maria Souza", result.get(1).getName());
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no students exist")
    void findAllShouldReturnEmptyList() {
        when(studentRepository.findAll()).thenReturn(List.of());

        List<Student> result = findStudentUseCase.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return students matching name ignoring case and accents")
    void findByNameShouldReturnMatching() {
        Student student = buildStudent();
        when(studentRepository.findByNameIgnoreCaseAndAccents("joao")).thenReturn(List.of(student));

        List<Student> result = findStudentUseCase.findByName("joao");

        assertEquals(1, result.size());
        assertEquals("João Silva", result.getFirst().getName());
        verify(studentRepository).findByNameIgnoreCaseAndAccents("joao");
    }

    @Test
    @DisplayName("Should return empty list when no student matches the name")
    void findByNameShouldReturnEmptyWhenNoMatch() {
        when(studentRepository.findByNameIgnoreCaseAndAccents("xyz")).thenReturn(List.of());

        List<Student> result = findStudentUseCase.findByName("xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return students with overdue payments")
    void findByPaymentStatusShouldReturnMatching() {
        Student student = buildStudent();
        when(studentRepository.findByPaymentStatus("Vencido")).thenReturn(List.of(student));

        List<Student> result = findStudentUseCase.findByPaymentStatus("Vencido");

        assertEquals(1, result.size());
        verify(studentRepository).findByPaymentStatus("Vencido");
    }

    @Test
    @DisplayName("Should return empty list when no student has the given payment status")
    void findByPaymentStatusShouldReturnEmptyWhenNoMatch() {
        when(studentRepository.findByPaymentStatus("Pago")).thenReturn(List.of());

        List<Student> result = findStudentUseCase.findByPaymentStatus("Pago");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should calculate age correctly from birth date in StudentResponse")
    void studentResponseShouldCalculateAgeFromBirthDate() {
        Student student = buildStudent();

        StudentResponse response = StudentResponse.from(student);

        assertNotNull(response);
        assertEquals("João Silva", response.name());
        assertEquals("12345678901", response.cpf());
        assertTrue(response.age() > 0, "Age should be positive");
    }

    @Test
    @DisplayName("Should return students within the age range")
    void findByAgeRangeShouldReturnMatching() {
        Student student = buildStudent();
        when(studentRepository.findByAgeRange(20, 35)).thenReturn(List.of(student));

        List<Student> result = findStudentUseCase.findByAgeRange(20, 35);

        assertEquals(1, result.size());
        verify(studentRepository).findByAgeRange(20, 35);
    }

    @Test
    @DisplayName("Should return only active students")
    void findByActiveShouldReturnActiveStudents() {
        Student student = buildStudent();
        when(studentRepository.findByActive(true)).thenReturn(List.of(student));

        List<Student> result = findStudentUseCase.findByActive(true);

        assertEquals(1, result.size());
        assertTrue(result.getFirst().isActive());
        verify(studentRepository).findByActive(true);
    }

    @Test
    @DisplayName("Should return only inactive students")
    void findByActiveShouldReturnInactiveStudents() {
        Student inactive = new Student();
        inactive.setId(2L);
        inactive.setName("Carlos Inativo");
        inactive.setCpf("11111111111");
        inactive.setBirthDate(LocalDate.of(1990, 1, 1));
        inactive.setActive(false);

        when(studentRepository.findByActive(false)).thenReturn(List.of(inactive));

        List<Student> result = findStudentUseCase.findByActive(false);

        assertEquals(1, result.size());
        assertFalse(result.getFirst().isActive());
    }

    @Test
    @DisplayName("Should return subscriptions for a given student")
    void findSubscriptionsByStudentShouldReturnSubscriptions() {
        Subscription sub = new Subscription();
        sub.setId(1L);
        when(subscriptionRepository.findAllByStudentId(1L)).thenReturn(List.of(sub));

        List<Subscription> result = findStudentUseCase.findSubscriptionsByStudent(1L);

        assertEquals(1, result.size());
        verify(subscriptionRepository).findAllByStudentId(1L);
    }

    @Test
    @DisplayName("Should create student when CPF is unique")
    void createShouldSucceedWhenCpfIsUnique() {
        Student student = buildStudent();
        when(studentRepository.existsByCpf("12345678901")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = createStudentUseCase.execute(student);

        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        verify(studentRepository).existsByCpf("12345678901");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ConflictException when CPF already exists")
    void createShouldThrowConflictWhenCpfExists() {
        Student student = buildStudent();
        when(studentRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(ConflictException.class, () -> createStudentUseCase.execute(student));
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should resolve plan when planId is provided on create")
    void createShouldResolvePlanWhenPlanIdIsProvided() {
        Plan plan = new Plan();
        plan.setId(2L);
        plan.setName("Mensal");

        Student student = buildStudent();
        student.setPlanId(2L);

        when(studentRepository.existsByCpf("12345678901")).thenReturn(false);
        when(planRepository.findById(2L)).thenReturn(Optional.of(plan));
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = createStudentUseCase.execute(student);

        assertNotNull(result.getPlanId());
        assertEquals(2L, result.getPlanId());
        verify(planRepository).findById(2L);
    }

    @Test
    @DisplayName("Should not look up plan when plan is null on create")
    void createShouldNotCallPlanRepoWhenPlanIsNull() {
        Student student = buildStudent();
        student.setPlanId(null);

        when(studentRepository.existsByCpf("12345678901")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        createStudentUseCase.execute(student);

        verifyNoInteractions(planRepository);
    }

    @Test
    @DisplayName("Should update student successfully")
    void updateShouldSucceed() {
        Student existing = buildStudent();
        Student incoming = new Student();
        incoming.setName("João Atualizado");
        incoming.setCpf("12345678901");
        incoming.setBirthDate(LocalDate.of(1995, 5, 20));
        incoming.setPhone("11988888888");
        incoming.setEmail("joao.novo@email.com");
        incoming.setAddress("Av. Brasil, 500");
        incoming.setActive(true);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByCpfAndIdNot("12345678901", 1L)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        Student result = updateStudentUseCase.execute(1L, incoming);

        assertNotNull(result);
        assertEquals("João Atualizado", result.getName());
        assertEquals("11988888888", result.getPhone());
        verify(studentRepository).save(existing);
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent student")
    void updateShouldThrowNotFoundWhenMissing() {
        Student incoming = buildStudent();
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> updateStudentUseCase.execute(99L, incoming));
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ConflictException when CPF belongs to another student on update")
    void updateShouldThrowConflictWhenCpfConflicts() {
        Student existing = buildStudent();
        Student incoming = new Student();
        incoming.setName("João Silva");
        incoming.setCpf("99999999999");
        incoming.setBirthDate(LocalDate.of(1995, 5, 20));

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByCpfAndIdNot("99999999999", 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> updateStudentUseCase.execute(1L, incoming));
        verify(studentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete student successfully when ID exists")
    void deleteShouldSucceed() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        deleteStudentUseCase.execute(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when student does not exist on delete")
    void deleteShouldThrowNotFoundWhenMissing() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> deleteStudentUseCase.execute(99L));
        verify(studentRepository, never()).deleteById(any());
    }
}
