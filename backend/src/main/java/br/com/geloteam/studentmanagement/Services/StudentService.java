package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Student;
import br.com.geloteam.studentmanagement.Models.Subscription;
import br.com.geloteam.studentmanagement.Repositories.StudentRepository;
import br.com.geloteam.studentmanagement.Repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Student findById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + id));
    }

    public List<Subscription> getSubscriptionsByStudent(Long studentId) {
        return subscriptionRepository.findAllByStudentId(studentId);
    }

}
