package br.com.geloteam.studentmanagement.Controllers;

import br.com.geloteam.studentmanagement.DTO.PlanDTO;
import br.com.geloteam.studentmanagement.Models.Plan;
import br.com.geloteam.studentmanagement.Services.PlanService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PlanController {

    @Autowired
    PlanService planService;

    @PostMapping("/plan")
    public Plan savePlan(@RequestBody @Valid PlanDTO planDTO) {
        var plan = new Plan();
        BeanUtils.copyProperties(planDTO, plan);

        return planService.save(plan);
    }

    @GetMapping("/plan")
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok().body(planService.findAllPlans());
    }

    @GetMapping("plan/{id}")
    public ResponseEntity<Plan> findById(@PathVariable Long id) {
        Plan plan = this.planService.findById(id);
        return ResponseEntity.ok().body(plan);
    }

    @PutMapping("/plan/{id}")
    public ResponseEntity<Void> updatePlan(@Valid @RequestBody Plan plan, @PathVariable Long id) {
        plan.setId(id);
        this.planService.update(plan);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/plan/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        this.planService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
