package br.com.geloteam.studentmanagement.application.plan;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import br.com.geloteam.studentmanagement.domain.plan.port.in.DeletePlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.FindPlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.SavePlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.UpdatePlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.out.PlanRepositoryPort;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PlanUseCaseImpl implements SavePlanUseCase, UpdatePlanUseCase, DeletePlanUseCase, FindPlanUseCase {

    private final PlanRepositoryPort planRepository;

    public PlanUseCaseImpl(PlanRepositoryPort planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Plan findById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plano não encontrado com ID: " + id));
    }

    @Override
    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    @Override
    @Transactional
    public Plan execute(Plan plan) {
        Plan saved = planRepository.save(plan);
        log.info("Plan created: {}", saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public Plan execute(Long id, Plan incoming) {
        Plan plan = findById(id);
        plan.setName(incoming.getName());
        plan.setMonthlyAmount(incoming.getMonthlyAmount());
        plan.setFrequency(incoming.getFrequency());
        Plan saved = planRepository.save(plan);
        log.info("Plan {} updated", saved.getName());
        return saved;
    }

    @Override
    @Transactional
    public void execute(Long id) {
        if (!planRepository.existsById(id)) {
            throw new NotFoundException("Este plano não existe ou já foi excluído!");
        }
        planRepository.deleteById(id);
        log.info("Plan {} deleted", id);
    }
}
