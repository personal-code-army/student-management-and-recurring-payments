package br.com.geloteam.studentmanagement.infrastructure.persistence.student;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepositoryAdapter implements StudentRepositoryPort {

    private final StudentJpaRepository jpa;

    public StudentRepositoryAdapter(StudentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Student> findById(Long id) { return jpa.findById(id); }
    @Override public List<Student> findAll() { return jpa.findAll(); }
    @Override public Student save(Student student) { return jpa.save(student); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
    @Override public boolean existsByCpf(String cpf) { return jpa.existsByCpf(cpf); }
    @Override public boolean existsByCpfAndIdNot(String cpf, Long id) { return jpa.existsByCpfAndIdNot(cpf, id); }
    @Override public List<Student> findByNameIgnoreCaseAndAccents(String name) { return jpa.findByNameIgnoreCaseAndAccents(name); }
    @Override public List<Student> findByPaymentStatus(String status) { return jpa.findByPaymentStatus(status); }
    @Override public List<Student> findByAgeRange(int minAge, int maxAge) { return jpa.findByAgeRange(minAge, maxAge); }
    @Override public List<Student> findByActive(boolean active) { return jpa.findByActive(active); }
}
