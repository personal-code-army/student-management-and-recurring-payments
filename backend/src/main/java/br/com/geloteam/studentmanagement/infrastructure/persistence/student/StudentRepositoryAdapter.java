package br.com.geloteam.studentmanagement.infrastructure.persistence.student;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.out.StudentRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.persistence.plan.PlanJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepositoryAdapter implements StudentRepositoryPort {

    private final StudentJpaRepository jpa;

    public StudentRepositoryAdapter(StudentJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override public Optional<Student> findById(Long id) { return jpa.findById(id).map(this::toDomain); }
    @Override public List<Student> findAll() { return jpa.findAll().stream().map(this::toDomain).toList(); }
    @Override public Student save(Student student) { return toDomain(jpa.save(toJpaEntity(student))); }
    @Override public void deleteById(Long id) { jpa.deleteById(id); }
    @Override public boolean existsById(Long id) { return jpa.existsById(id); }
    @Override public boolean existsByCpf(String cpf) { return jpa.existsByCpf(cpf); }
    @Override public boolean existsByCpfAndIdNot(String cpf, Long id) { return jpa.existsByCpfAndIdNot(cpf, id); }
    @Override public List<Student> findByNameIgnoreCaseAndAccents(String name) { return jpa.findByNameIgnoreCaseAndAccents(name).stream().map(this::toDomain).toList(); }
    @Override public List<Student> findByPaymentStatus(String status) { return jpa.findByPaymentStatus(status).stream().map(this::toDomain).toList(); }
    @Override public List<Student> findByAgeRange(int minAge, int maxAge) { return jpa.findByAgeRange(minAge, maxAge).stream().map(this::toDomain).toList(); }
    @Override public List<Student> findByActive(boolean active) { return jpa.findByActive(active).stream().map(this::toDomain).toList(); }

    private Student toDomain(StudentJpaEntity e) {
        Student s = new Student();
        s.setId(e.getId());
        s.setName(e.getName());
        s.setCpf(e.getCpf());
        s.setBirthDate(e.getBirthDate());
        s.setPhone(e.getPhone());
        s.setEmail(e.getEmail());
        s.setAddress(e.getAddress());
        s.setPlanId(e.getPlan() != null ? e.getPlan().getId() : null);
        s.setActive(e.isActive());
        return s;
    }

    private StudentJpaEntity toJpaEntity(Student s) {
        StudentJpaEntity e = new StudentJpaEntity();
        e.setId(s.getId());
        e.setName(s.getName());
        e.setCpf(s.getCpf());
        e.setBirthDate(s.getBirthDate());
        e.setPhone(s.getPhone());
        e.setEmail(s.getEmail());
        e.setAddress(s.getAddress());
        if (s.getPlanId() != null) {
            PlanJpaEntity plan = new PlanJpaEntity();
            plan.setId(s.getPlanId());
            e.setPlan(plan);
        }
        e.setActive(s.isActive());
        return e;
    }
}
