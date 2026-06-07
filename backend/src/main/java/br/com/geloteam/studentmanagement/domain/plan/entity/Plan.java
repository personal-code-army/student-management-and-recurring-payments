package br.com.geloteam.studentmanagement.domain.plan.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Plan {
    private Long id;
    private String name;
    private Double monthlyAmount;
    private Integer frequency;
}
