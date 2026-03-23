package br.com.geloteam.studentmanagement.Repositories;

import br.com.geloteam.studentmanagement.Models.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
