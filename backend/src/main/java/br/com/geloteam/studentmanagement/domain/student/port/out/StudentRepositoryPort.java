package br.com.geloteam.studentmanagement.domain.student.port.out;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepositoryPort {
    Optional<Student> findById(Long id);
    List<Student> findAll();
    Student save(Student student);
    void deleteById(Long id);
    boolean existsById(Long id);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);
    List<Student> findByNameIgnoreCaseAndAccents(String name);
    List<Student> findByPaymentStatus(String status);
    List<Student> findByAgeRange(int minAge, int maxAge);
    List<Student> findByActive(boolean active);
}
