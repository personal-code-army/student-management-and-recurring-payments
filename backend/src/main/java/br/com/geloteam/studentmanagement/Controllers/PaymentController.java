package br.com.geloteam.studentmanagement.Controllers;

import br.com.geloteam.studentmanagement.DTO.PaymentDTO;
import br.com.geloteam.studentmanagement.Models.Payment;
import br.com.geloteam.studentmanagement.Models.Subscription;
import br.com.geloteam.studentmanagement.Services.PaymentService;
import br.com.geloteam.studentmanagement.Services.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;
    @Autowired
    SubscriptionService subscriptionService;

    @PostMapping("/payment")
    public Payment savePayment(@RequestBody @Valid PaymentDTO paymentDTO){

        var payment = new Payment();
        BeanUtils.copyProperties(paymentDTO, payment);

        Subscription subscription = subscriptionService.findById(paymentDTO.getSubscription().getId());

        payment.setSubscription(subscription);

        return paymentService.save(payment);

    }

    @GetMapping("/payment")
    public ResponseEntity<List<Payment>> getAllPayments(){

        List<Payment> payments = paymentService.findAllPayments();

        if (payments.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok().body(payments);

    }

    @GetMapping("/payment/{id}")
    public ResponseEntity<Payment> findPaymentById(@PathVariable Long id){

        Payment payment = this.paymentService.findById(id);
        return ResponseEntity.ok().body(payment);

    }

    @PutMapping("/payment/{id}")
    public ResponseEntity<Void> updatePayment(@Valid @RequestBody Payment payment, @PathVariable Long id){

        payment.setId(id);
        this.paymentService.update(payment);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/payment/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id){

        this.paymentService.delete(id);
        return ResponseEntity.noContent().build();

    }

}
