package br.com.geloteam.studentmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private double value;
    private String paymentMethod;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private String status;
    //    @ManyToOne(fetch = FetchType.LAZY)
    //    @JoinColumn(name = "subscription_id", nullable = false)
    //    private Subscription subscription;


}
