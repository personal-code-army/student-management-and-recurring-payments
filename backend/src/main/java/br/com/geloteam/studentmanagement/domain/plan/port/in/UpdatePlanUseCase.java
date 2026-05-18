package br.com.geloteam.studentmanagement.domain.plan.port.in;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;

public interface UpdatePlanUseCase {
    Plan execute(Long id, Plan plan);
}
