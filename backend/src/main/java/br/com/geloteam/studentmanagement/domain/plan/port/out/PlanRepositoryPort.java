package br.com.geloteam.studentmanagement.domain.plan.port.out;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;

import java.util.List;
import java.util.Optional;

public interface PlanRepositoryPort {
    Optional<Plan> findById(Long id);
    List<Plan> findAll();
    Plan save(Plan plan);
    void deleteById(Long id);
    boolean existsById(Long id);
}
