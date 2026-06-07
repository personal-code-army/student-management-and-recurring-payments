package br.com.geloteam.studentmanagement.infrastructure.web.subscription;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.DeleteSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.FindSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.SaveSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.UpdateSubscriptionUseCase;
import br.com.geloteam.studentmanagement.infrastructure.web.subscription.dto.SubscriptionRequest;
import br.com.geloteam.studentmanagement.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SaveSubscriptionUseCase saveSubscriptionUseCase;
    private final UpdateSubscriptionUseCase updateSubscriptionUseCase;
    private final DeleteSubscriptionUseCase deleteSubscriptionUseCase;
    private final FindSubscriptionUseCase findSubscriptionUseCase;

    public SubscriptionController(SaveSubscriptionUseCase saveSubscriptionUseCase,
                                  UpdateSubscriptionUseCase updateSubscriptionUseCase,
                                  DeleteSubscriptionUseCase deleteSubscriptionUseCase,
                                  FindSubscriptionUseCase findSubscriptionUseCase) {
        this.saveSubscriptionUseCase = saveSubscriptionUseCase;
        this.updateSubscriptionUseCase = updateSubscriptionUseCase;
        this.deleteSubscriptionUseCase = deleteSubscriptionUseCase;
        this.findSubscriptionUseCase = findSubscriptionUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Subscription>> create(@RequestBody @Valid SubscriptionRequest request) {
        Subscription subscription = toEntity(request);
        Subscription saved = saveSubscriptionUseCase.execute(subscription);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.data(saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Subscription>>> getAll(
            @RequestParam(required = false) String studentName
    ) {
        List<Subscription> subscriptions = studentName != null
                ? findSubscriptionUseCase.findByStudentName(studentName)
                : findSubscriptionUseCase.findAll();
        return ResponseEntity.ok(ApiResponse.data(subscriptions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Subscription>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.data(findSubscriptionUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Subscription>> update(@PathVariable Long id,
                                                            @RequestBody @Valid SubscriptionRequest request) {
        Subscription incoming = toEntity(request);
        Subscription updated = updateSubscriptionUseCase.execute(id, incoming);
        return ResponseEntity.ok(ApiResponse.data(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        deleteSubscriptionUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    private Subscription toEntity(SubscriptionRequest request) {
        Subscription subscription = new Subscription();
        subscription.setStartDate(request.startDate());
        subscription.setStatus(request.status());
        subscription.setPaymentMethod(request.paymentMethod());
        subscription.setStudentId(request.studentId());
        subscription.setPlanId(request.planId());
        return subscription;
    }
}
