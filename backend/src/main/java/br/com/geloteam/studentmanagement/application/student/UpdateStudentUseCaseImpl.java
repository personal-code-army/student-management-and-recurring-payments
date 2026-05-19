package br.com.geloteam.studentmanagement.application.student;

import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.in.UpdateStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateStudentUseCaseImpl implements UpdateStudentUseCase {

    private final StudentRepositoryPort studentRepository;
    private final PlanRepositoryPort planRepository;

    public UpdateStudentUseCaseImpl(StudentRepositoryPort studentRepository,
                                    PlanRepositoryPort planRepository) {
        this.studentRepository = studentRepository;
        this.planRepository = planRepository;
    }

    @Override
    @Transactional
    public Student execute(Long id, Student incoming) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado com ID: " + id));

        if (studentRepository.existsByCpfAndIdNot(incoming.getCpf(), id)) {
            throw new ConflictException("CPF_ALREADY_REGISTERED", "CPF já cadastrado: " + incoming.getCpf());
        }

        student.setName(incoming.getName());
        student.setCpf(incoming.getCpf());
        student.setBirthDate(incoming.getBirthDate());
        student.setPhone(incoming.getPhone());
        student.setEmail(incoming.getEmail());
        student.setAddress(incoming.getAddress());
        student.setActive(incoming.isActive());

        if (incoming.getPlan() != null && incoming.getPlan().getId() != null) {
            var plan = planRepository.findById(incoming.getPlan().getId())
                    .orElseThrow(() -> new NotFoundException("Plano não encontrado com ID: " + incoming.getPlan().getId()));
            student.setPlan(plan);
        } else {
            student.setPlan(null);
        }

        Student saved = studentRepository.save(student);
        log.info("Aluno atualizado: {}", saved.getName());
        return saved;
    }
}
