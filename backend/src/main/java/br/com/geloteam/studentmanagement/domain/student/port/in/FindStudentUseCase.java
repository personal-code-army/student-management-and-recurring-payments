package br.com.geloteam.studentmanagement.domain.student.port.in;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;

import java.util.List;

public interface FindStudentUseCase {
    Student findById(Long id);
    List<Student> findAll();
    List<Student> findByName(String name);
    List<Student> findByPaymentStatus(String status);
    List<Student> findByAgeRange(int minAge, int maxAge);
    List<Student> findByActive(boolean active);
    List<Subscription> findSubscriptionsByStudent(Long studentId);
}
