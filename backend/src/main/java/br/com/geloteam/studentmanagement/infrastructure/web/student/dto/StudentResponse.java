package br.com.geloteam.studentmanagement.infrastructure.web.student.dto;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;

import java.time.LocalDate;
import java.time.Period;

public record StudentResponse(
        Long id,
        String name,
        String cpf,
        LocalDate birthDate,
        Integer age,
        String phone,
        String email,
        String address,
        Long planId,
        boolean active
) {
    public static StudentResponse from(Student s) {
        LocalDate birth = s.getBirthDate();
        return new StudentResponse(
                s.getId(),
                s.getName(),
                s.getCpf(),
                birth,
                birth != null ? Period.between(birth, LocalDate.now()).getYears() : null,
                s.getPhone(),
                s.getEmail(),
                s.getAddress(),
                s.getPlanId(),
                s.isActive()
        );
    }
}
