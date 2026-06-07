package br.com.geloteam.studentmanagement.application.student;

import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.in.CreateStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.SaveSubscriptionUseCase;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class CreateStudentUseCaseImpl implements CreateStudentUseCase {

    private final StudentRepositoryPort studentRepository;
    private final PlanRepositoryPort planRepository;
    private final SaveSubscriptionUseCase saveSubscriptionUseCase;

    public CreateStudentUseCaseImpl(StudentRepositoryPort studentRepository,
                                    PlanRepositoryPort planRepository,
                                    SaveSubscriptionUseCase saveSubscriptionUseCase) {
        this.studentRepository = studentRepository;
        this.planRepository = planRepository;
        this.saveSubscriptionUseCase = saveSubscriptionUseCase;
    }

    @Override
    @Transactional
    public Student execute(Student student) {
        if (studentRepository.existsByCpf(student.getCpf())) {
            throw new ConflictException("CPF_ALREADY_REGISTERED", "CPF já cadastrado: " + student.getCpf());
        }

        if (student.getPlanId() != null) {
            planRepository.findById(student.getPlanId())
                    .orElseThrow(() -> new NotFoundException("Plano não encontrado com ID: " + student.getPlanId()));
        }

        Student saved = studentRepository.save(student);
        log.info("Aluno criado: {}", saved.getName());

        if (saved.getPlanId() != null) {
            Subscription subscription = new Subscription();
            subscription.setStudentId(saved.getId());
            subscription.setPlanId(saved.getPlanId());
            subscription.setStartDate(LocalDate.now());
            subscription.setStatus("Ativo");
            saveSubscriptionUseCase.execute(subscription);
            log.info("Assinatura criada automaticamente para o aluno: {}", saved.getName());
        }

        return saved;
    }
}
