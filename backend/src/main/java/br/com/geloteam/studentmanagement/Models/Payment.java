package br.com.geloteam.studentmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id",nullable = false, unique = true)
    private Subscription subscription;

    private String description;
    private double value;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "issue_date")
    private LocalDate issueDate;
    private String status;


}
