package br.com.geloteam.studentmanagement.domain.subscription.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor
public class Subscription {
    private Long id;
    private Long studentId;
    private Long planId;
    private LocalDate startDate;
    private String status;
    private String paymentMethod;
}
