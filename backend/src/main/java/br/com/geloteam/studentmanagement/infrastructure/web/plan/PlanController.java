package br.com.geloteam.studentmanagement.infrastructure.web.plan;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import br.com.geloteam.studentmanagement.domain.plan.port.in.DeletePlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.FindPlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.SavePlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.UpdatePlanUseCase;
import br.com.geloteam.studentmanagement.infrastructure.web.plan.dto.PlanRequest;
import br.com.geloteam.studentmanagement.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
public class PlanController {

    private final SavePlanUseCase savePlanUseCase;
    private final UpdatePlanUseCase updatePlanUseCase;
    private final DeletePlanUseCase deletePlanUseCase;
    private final FindPlanUseCase findPlanUseCase;

    public PlanController(SavePlanUseCase savePlanUseCase,
                          UpdatePlanUseCase updatePlanUseCase,
                          DeletePlanUseCase deletePlanUseCase,
                          FindPlanUseCase findPlanUseCase) {
        this.savePlanUseCase = savePlanUseCase;
        this.updatePlanUseCase = updatePlanUseCase;
        this.deletePlanUseCase = deletePlanUseCase;
        this.findPlanUseCase = findPlanUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Plan>> create(@RequestBody @Valid PlanRequest request) {
        Plan plan = new Plan();
        plan.setName(request.name());
        plan.setMonthlyAmount(request.monthlyAmount());
        plan.setFrequency(request.frequency() != null ? request.frequency() : 0);
        Plan saved = savePlanUseCase.execute(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.data(saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Plan>>> getAll() {
        return ResponseEntity.ok(ApiResponse.data(findPlanUseCase.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Plan>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.data(findPlanUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Plan>> update(@PathVariable Long id,
                                                    @RequestBody @Valid PlanRequest request) {
        Plan incoming = new Plan();
        incoming.setName(request.name());
        incoming.setMonthlyAmount(request.monthlyAmount());
        incoming.setFrequency(request.frequency() != null ? request.frequency() : 0);
        Plan updated = updatePlanUseCase.execute(id, incoming);
        return ResponseEntity.ok(ApiResponse.data(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        deletePlanUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
