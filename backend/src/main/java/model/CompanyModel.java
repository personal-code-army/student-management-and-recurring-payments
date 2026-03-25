package model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "Company")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(nullable = false,length = 50)
    private String name;

    @Column(nullable = false,length = 14)
    private String cnpj;

    @Column(nullable = false,length = 8)
    private String cep;

    @Column(nullable = false,length = 50)
    private String address;

    @Column(nullable = false,length = 20)
    private String city;

    @Column(nullable = false,length = 13)
    private String telephone;

}
