package br.com.geloteam.studentmanagement.Models;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.apachecommons.CommonsLog;

import java.time.LocalDate;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "monthly_amount")
    private double monthlyAmount;
    private int frequency;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "company_id",nullable = false, unique = true)
//    private Company company;
}
