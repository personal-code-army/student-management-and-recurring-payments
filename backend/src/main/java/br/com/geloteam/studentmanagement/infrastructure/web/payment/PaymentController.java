package br.com.geloteam.studentmanagement.infrastructure.web.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.in.DeletePaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.FindPaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.GenerateMercadoPagoLinkUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.SavePaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.UpdatePaymentUseCase;
import br.com.geloteam.studentmanagement.infrastructure.web.payment.dto.GeneratePaymentLinkRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.payment.dto.PaymentLinkResponse;
import br.com.geloteam.studentmanagement.infrastructure.web.payment.dto.PaymentRequest;
import br.com.geloteam.studentmanagement.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final SavePaymentUseCase savePaymentUseCase;
    private final UpdatePaymentUseCase updatePaymentUseCase;
    private final DeletePaymentUseCase deletePaymentUseCase;
    private final FindPaymentUseCase findPaymentUseCase;
    private final GenerateMercadoPagoLinkUseCase generateMercadoPagoLinkUseCase;

    public PaymentController(SavePaymentUseCase savePaymentUseCase,
                             UpdatePaymentUseCase updatePaymentUseCase,
                             DeletePaymentUseCase deletePaymentUseCase,
                             FindPaymentUseCase findPaymentUseCase,
                             GenerateMercadoPagoLinkUseCase generateMercadoPagoLinkUseCase) {
        this.savePaymentUseCase = savePaymentUseCase;
        this.updatePaymentUseCase = updatePaymentUseCase;
        this.deletePaymentUseCase = deletePaymentUseCase;
        this.findPaymentUseCase = findPaymentUseCase;
        this.generateMercadoPagoLinkUseCase = generateMercadoPagoLinkUseCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Payment>> create(@RequestBody @Valid PaymentRequest request) {
        Payment payment = toEntity(request);
        Payment saved = savePaymentUseCase.execute(payment);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.data(saved));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAll(
            @RequestParam(required = false) Long subscriptionId,
            @RequestParam(required = false) String studentName
    ) {
        List<Payment> payments;
        if (subscriptionId != null) {
            payments = findPaymentUseCase.findBySubscription(subscriptionId);
        } else if (studentName != null) {
            payments = findPaymentUseCase.findByStudentName(studentName);
        } else {
            payments = findPaymentUseCase.findAll();
        }
        return ResponseEntity.ok(ApiResponse.data(payments));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.data(findPaymentUseCase.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Payment>> update(@PathVariable Long id,
                                                       @RequestBody @Valid PaymentRequest request) {
        Payment incoming = toEntity(request);
        Payment updated = updatePaymentUseCase.execute(id, incoming);
        return ResponseEntity.ok(ApiResponse.data(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        deletePaymentUseCase.execute(id);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping("/mercadopago-link")
    public ResponseEntity<ApiResponse<PaymentLinkResponse>> generateMercadoPagoLink(
            @RequestBody @Valid GeneratePaymentLinkRequest request
    ) {
        GenerateMercadoPagoLinkUseCase.PaymentLinkResult result =
                generateMercadoPagoLinkUseCase.execute(request.subscriptionId());
        PaymentLinkResponse body = new PaymentLinkResponse(
                result.checkoutUrl(),
                result.expirationDate(),
                result.externalReference()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.data(body));
    }

    private Payment toEntity(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setDescription(request.description());
        payment.setValue(request.value());
        payment.setPaymentMethod(request.paymentMethod());
        payment.setDueDate(request.dueDate());
        payment.setIssueDate(request.issueDate());
        payment.setStatus(request.status());
        payment.setSubscriptionId(request.subscriptionId());
        return payment;
    }
}
