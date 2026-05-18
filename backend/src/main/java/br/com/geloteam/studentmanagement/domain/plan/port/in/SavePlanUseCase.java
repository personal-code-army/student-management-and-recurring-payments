package br.com.geloteam.studentmanagement.domain.plan.port.in;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;

public interface SavePlanUseCase {
    Plan execute(Plan plan);
}
