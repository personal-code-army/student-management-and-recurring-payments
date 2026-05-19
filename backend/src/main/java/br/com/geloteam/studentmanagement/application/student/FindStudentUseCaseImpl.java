package br.com.geloteam.studentmanagement.application.student;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.in.FindStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.out.SubscriptionRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindStudentUseCaseImpl implements FindStudentUseCase {

    private final StudentRepositoryPort studentRepository;
    private final SubscriptionRepositoryPort subscriptionRepository;

    public FindStudentUseCaseImpl(StudentRepositoryPort studentRepository,
                                  SubscriptionRepositoryPort subscriptionRepository) {
        this.studentRepository = studentRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado com ID: " + id));
    }

    @Override
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    public List<Student> findByName(String name) {
        return studentRepository.findByNameIgnoreCaseAndAccents(name);
    }

    @Override
    public List<Student> findByPaymentStatus(String status) {
        return studentRepository.findByPaymentStatus(status);
    }

    @Override
    public List<Student> findByAgeRange(int minAge, int maxAge) {
        return studentRepository.findByAgeRange(minAge, maxAge);
    }

    @Override
    public List<Student> findByActive(boolean active) {
        return studentRepository.findByActive(active);
    }

    @Override
    public List<Subscription> findSubscriptionsByStudent(Long studentId) {
        return subscriptionRepository.findAllByStudentId(studentId);
    }
}
