package br.com.geloteam.studentmanagement.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Company {
    private Long id;
    private String cnpj;
    private String name;
    private String cep;
    private String address;
    private String city;
    private String cellphoneNumber;
}
