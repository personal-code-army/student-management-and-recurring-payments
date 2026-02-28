package br.com.geloteam.studentmanagement.Controllers;

import br.com.geloteam.studentmanagement.DTO.SubscriptionDTO;
import br.com.geloteam.studentmanagement.Models.Plan;
import br.com.geloteam.studentmanagement.Models.Subscription;
import br.com.geloteam.studentmanagement.Services.PlanService;
import br.com.geloteam.studentmanagement.Services.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SubscriptionController {

    @Autowired
    SubscriptionService subscriptionService;

    @Autowired
    PlanService planService;

    @PostMapping("/subscription")
    public Subscription saveSubscription(@RequestBody @Valid SubscriptionDTO subscriptionDTO) {
        var subscription = new Subscription();
        BeanUtils.copyProperties(subscriptionDTO, subscription);

        Plan plan = planService.findById(subscriptionDTO.getPlan().getId());
        //Student student = studentService.findById(subscriptionDTO.getStudent().getId());

        return subscriptionService.save(subscription);
    }

    @GetMapping("/subscription")
    public ResponseEntity<List<Subscription>> getAllSubscriptions() {
        return ResponseEntity.ok().body(subscriptionService.findAllSubscriptions());
    }

    @GetMapping("subscription/{id}")
    public ResponseEntity<Subscription> findById(@PathVariable Long id) {
        Subscription subscription = this.subscriptionService.findById(id);
        return ResponseEntity.ok().body(subscription);
    }

    @PutMapping("/subscription/{id}")
    public ResponseEntity<Void> updateSubscription(@Valid @RequestBody Subscription subscription, @PathVariable Long id) {
        subscription.setId(id);
        this.subscriptionService.update(subscription);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/subscription/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable Long id) {
        this.subscriptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
