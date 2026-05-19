package br.com.geloteam.studentmanagement.application.student;

import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.in.CreateStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CreateStudentUseCaseImpl implements CreateStudentUseCase {

    private final StudentRepositoryPort studentRepository;
    private final PlanRepositoryPort planRepository;

    public CreateStudentUseCaseImpl(StudentRepositoryPort studentRepository,
                                    PlanRepositoryPort planRepository) {
        this.studentRepository = studentRepository;
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public Student execute(Student student) {
        if (studentRepository.existsByCpf(student.getCpf())) {
            throw new ConflictException("CPF_ALREADY_REGISTERED", "CPF já cadastrado: " + student.getCpf());
        }

        if (student.getPlan() != null && student.getPlan().getId() != null) {
            var plan = planRepository.findById(student.getPlan().getId())
                    .orElseThrow(() -> new NotFoundException("Plano não encontrado com ID: " + student.getPlan().getId()));
            student.setPlan(plan);
        }

        Student saved = studentRepository.save(student);
        log.info("Aluno criado: {}", saved.getName());
        return saved;
    }
}
