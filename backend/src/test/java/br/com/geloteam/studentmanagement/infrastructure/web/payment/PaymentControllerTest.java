package br.com.geloteam.studentmanagement.infrastructure.web.payment;

import br.com.geloteam.studentmanagement.domain.payment.entity.Payment;
import br.com.geloteam.studentmanagement.domain.payment.port.in.DeletePaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.FindPaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.SavePaymentUseCase;
import br.com.geloteam.studentmanagement.domain.payment.port.in.UpdatePaymentUseCase;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean SavePaymentUseCase savePaymentUseCase;
    @MockitoBean UpdatePaymentUseCase updatePaymentUseCase;
    @MockitoBean DeletePaymentUseCase deletePaymentUseCase;
    @MockitoBean FindPaymentUseCase findPaymentUseCase;

    private Payment buildPayment(Long id) {
        Payment p = new Payment();
        p.setId(id);
        p.setSubscriptionId(1L);
        p.setDescription("Mensalidade Janeiro");
        p.setValue(99.90);
        p.setPaymentMethod("PIX");
        p.setDueDate(LocalDate.of(2026, 1, 10));
        p.setStatus("PENDING");
        return p;
    }

    private String paymentJson() {
        return """
                {
                  "description":"Mensalidade Janeiro",
                  "value":99.90,
                  "paymentMethod":"PIX",
                  "dueDate":"2026-01-10",
                  "status":"PENDING",
                  "subscriptionId":1
                }
                """;
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("POST /api/payments - should return 201 with created payment")
    void createSuccess() throws Exception {
        when(savePaymentUseCase.execute(any(Payment.class))).thenReturn(buildPayment(1L));

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.description").value("Mensalidade Janeiro"))
                .andExpect(jsonPath("$.data.value").value(99.90))
                .andExpect(jsonPath("$.data.paymentMethod").value("PIX"));
    }

    @Test
    @DisplayName("POST /api/payments - should return 400 when description is missing")
    void createMissingDescription() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"value":99.90,"paymentMethod":"PIX","dueDate":"2026-01-10","status":"PENDING","subscriptionId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/payments - should return 400 when subscriptionId is missing")
    void createMissingSubscriptionId() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"X","value":10.0,"paymentMethod":"PIX","dueDate":"2026-01-10","status":"PENDING"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/payments - should return 200 with all payments")
    void getAllSuccess() throws Exception {
        when(findPaymentUseCase.findAll()).thenReturn(List.of(buildPayment(1L), buildPayment(2L)));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/payments?subscriptionId=1 - should filter by subscription")
    void getAllFilterBySubscription() throws Exception {
        when(findPaymentUseCase.findBySubscription(1L)).thenReturn(List.of(buildPayment(1L)));

        mockMvc.perform(get("/api/payments").param("subscriptionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].subscriptionId").value(1));
    }

    @Test
    @DisplayName("GET /api/payments?studentName=João - should filter by student name")
    void getAllFilterByStudentName() throws Exception {
        when(findPaymentUseCase.findByStudentName("João")).thenReturn(List.of(buildPayment(1L)));

        mockMvc.perform(get("/api/payments").param("studentName", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/payments/{id} - should return 200 with payment")
    void getByIdSuccess() throws Exception {
        when(findPaymentUseCase.findById(1L)).thenReturn(buildPayment(1L));

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("GET /api/payments/{id} - should return 404 when not found")
    void getByIdNotFound() throws Exception {
        when(findPaymentUseCase.findById(99L))
                .thenThrow(new NotFoundException("Pagamento não encontrado com ID: 99"));

        mockMvc.perform(get("/api/payments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("PUT /api/payments/{id} - should return 200 with updated payment")
    void updateSuccess() throws Exception {
        Payment updated = buildPayment(1L);
        updated.setStatus("PAID");
        when(updatePaymentUseCase.execute(eq(1L), any(Payment.class))).thenReturn(updated);

        mockMvc.perform(put("/api/payments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description":"Mensalidade Janeiro",
                                  "value":99.90,
                                  "paymentMethod":"PIX",
                                  "dueDate":"2026-01-10",
                                  "status":"PAID",
                                  "subscriptionId":1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));
    }

    @Test
    @DisplayName("PUT /api/payments/{id} - should return 404 when not found")
    void updateNotFound() throws Exception {
        when(updatePaymentUseCase.execute(eq(99L), any(Payment.class)))
                .thenThrow(new NotFoundException("Pagamento não encontrado com ID: 99"));

        mockMvc.perform(put("/api/payments/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson()))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/payments/{id} - should return 200 on success")
    void deleteSuccess() throws Exception {
        mockMvc.perform(delete("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/payments/{id} - should return 404 when not found")
    void deleteNotFound() throws Exception {
        doThrow(new NotFoundException("Pagamento não encontrado com ID: 99"))
                .when(deletePaymentUseCase).execute(99L);

        mockMvc.perform(delete("/api/payments/99"))
                .andExpect(status().isNotFound());
    }
}
