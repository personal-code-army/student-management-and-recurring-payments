package br.com.geloteam.studentmanagement.domain.student.port.in;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;

public interface CreateStudentUseCase {
    Student execute(Student student);
}
