package br.com.geloteam.studentmanagement.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class User {
    private Long id;
    private Long companyId;
    private String name;
    private String email;
    private String password;
    private String cellphoneNumber;
    private UserRole role;
}
