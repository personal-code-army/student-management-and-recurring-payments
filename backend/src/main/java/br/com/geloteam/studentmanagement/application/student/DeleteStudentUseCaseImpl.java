package br.com.geloteam.studentmanagement.application.student;

import br.com.geloteam.studentmanagement.domain.student.port.in.DeleteStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeleteStudentUseCaseImpl implements DeleteStudentUseCase {

    private final StudentRepositoryPort studentRepository;

    public DeleteStudentUseCaseImpl(StudentRepositoryPort studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new NotFoundException("Este aluno não existe ou já foi excluído!");
        }
        studentRepository.deleteById(id);
        log.info("Aluno {} excluído", id);
    }
}
