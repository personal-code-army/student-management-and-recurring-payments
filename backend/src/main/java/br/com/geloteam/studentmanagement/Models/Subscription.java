package br.com.geloteam.studentmanagement.Models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dueDate;
    private String status;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "plan_id", nullable = false)
//    private Plan plan;
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "student_id", nullable = false, unique = true)
//    private Student student;

}
