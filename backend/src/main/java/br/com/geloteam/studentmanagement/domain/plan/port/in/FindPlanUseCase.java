package br.com.geloteam.studentmanagement.domain.plan.port.in;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;

import java.util.List;

public interface FindPlanUseCase {
    Plan findById(Long id);
    List<Plan> findAll();
}
