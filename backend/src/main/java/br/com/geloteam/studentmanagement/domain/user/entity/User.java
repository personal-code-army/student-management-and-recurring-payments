package br.com.geloteam.studentmanagement.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
public class User {
    private Long id;
    private Long companyId;
    private String name;
    private String email;
    private String password;
    private String cellphoneNumber;
    private String cpf;
    private String resetToken;
    private LocalDateTime resetTokenExpiry;
    private UserRole role;
}
