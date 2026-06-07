package br.com.geloteam.studentmanagement.domain.student.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class Student {
    private Long id;
    private String name;
    private String cpf;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private String address;
    private Long planId;
    private boolean active;
}
