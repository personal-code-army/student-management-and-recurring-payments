package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.Models.Plan;
import br.com.geloteam.studentmanagement.Repositories.PlanRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PlanService {

    @Autowired
    PlanRepository planRepository;

    public Plan findById(Long id){
        Optional<Plan> plan = this.planRepository.findById(id);
        return plan.orElseThrow(() -> new ValidationException(
                "Plano não encontrado"
        ));
    }

    public List<Plan> findAllPlans() {
        return planRepository.findAll();
    }

//    public List<Plan> findAllUserPlan(String name) {
//        return planRepository.findAllBySubscriptionStudentName(name);
//    }

    @Transactional
    public Plan update(Plan plans){
        Plan plan = findById(plans.getId());
        Plan saved = this.planRepository.save(plan);
        log.info("Plan {} updated", saved.getName());
        return saved;
    }

    @Transactional
    public void delete(long id){
        planRepository.deleteById(id);
        log.info("Plan {} deleted", id);
    }

    @Transactional
    public Plan save(Plan plan){
        Plan saved = this.planRepository.save(plan);
        log.info("Plan created: {}", saved.getName());
        return saved;
    }

}
