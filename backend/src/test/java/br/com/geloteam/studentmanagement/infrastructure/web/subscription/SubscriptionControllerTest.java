package br.com.geloteam.studentmanagement.infrastructure.web.subscription;

import br.com.geloteam.studentmanagement.domain.subscription.entity.Subscription;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.DeleteSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.FindSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.SaveSubscriptionUseCase;
import br.com.geloteam.studentmanagement.domain.subscription.port.in.UpdateSubscriptionUseCase;
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

@WebMvcTest(SubscriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
class SubscriptionControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean SaveSubscriptionUseCase saveSubscriptionUseCase;
    @MockitoBean UpdateSubscriptionUseCase updateSubscriptionUseCase;
    @MockitoBean DeleteSubscriptionUseCase deleteSubscriptionUseCase;
    @MockitoBean FindSubscriptionUseCase findSubscriptionUseCase;

    private Subscription buildSubscription(Long id) {
        Subscription s = new Subscription();
        s.setId(id);
        s.setStudentId(1L);
        s.setPlanId(1L);
        s.setStartDate(LocalDate.of(2026, 1, 1));
        s.setStatus("ACTIVE");
        s.setPaymentMethod("PIX");
        return s;
    }

    private String subscriptionJson() {
        return """
                {
                  "startDate":"2026-01-01",
                  "status":"ACTIVE",
                  "paymentMethod":"PIX",
                  "planId":1,
                  "studentId":1
                }
                """;
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("POST /api/subscriptions - should return 201 with created subscription")
    void createSuccess() throws Exception {
        when(saveSubscriptionUseCase.execute(any(Subscription.class))).thenReturn(buildSubscription(1L));

        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.paymentMethod").value("PIX"));
    }

    @Test
    @DisplayName("POST /api/subscriptions - should return 400 when required fields missing")
    void createMissingFields() throws Exception {
        mockMvc.perform(post("/api/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"2026-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/subscriptions - should return 200 with all subscriptions")
    void getAllSuccess() throws Exception {
        when(findSubscriptionUseCase.findAll()).thenReturn(List.of(
                buildSubscription(1L), buildSubscription(2L)
        ));

        mockMvc.perform(get("/api/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("GET /api/subscriptions?studentName=João - should filter by student name")
    void getAllFilterByStudentName() throws Exception {
        when(findSubscriptionUseCase.findByStudentName("João"))
                .thenReturn(List.of(buildSubscription(1L)));

        mockMvc.perform(get("/api/subscriptions").param("studentName", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/subscriptions/{id} - should return 200 with subscription")
    void getByIdSuccess() throws Exception {
        when(findSubscriptionUseCase.findById(1L)).thenReturn(buildSubscription(1L));

        mockMvc.perform(get("/api/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/subscriptions/{id} - should return 404 when not found")
    void getByIdNotFound() throws Exception {
        when(findSubscriptionUseCase.findById(99L))
                .thenThrow(new NotFoundException("Assinatura não encontrada com ID: 99"));

        mockMvc.perform(get("/api/subscriptions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("PUT /api/subscriptions/{id} - should return 200 with updated subscription")
    void updateSuccess() throws Exception {
        Subscription updated = buildSubscription(1L);
        updated.setStatus("INACTIVE");
        when(updateSubscriptionUseCase.execute(eq(1L), any(Subscription.class))).thenReturn(updated);

        mockMvc.perform(put("/api/subscriptions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "startDate":"2026-01-01",
                                  "status":"INACTIVE",
                                  "paymentMethod":"PIX",
                                  "planId":1,
                                  "studentId":1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("PUT /api/subscriptions/{id} - should return 404 when not found")
    void updateNotFound() throws Exception {
        when(updateSubscriptionUseCase.execute(eq(99L), any(Subscription.class)))
                .thenThrow(new NotFoundException("Assinatura não encontrada com ID: 99"));

        mockMvc.perform(put("/api/subscriptions/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(subscriptionJson()))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/subscriptions/{id} - should return 200 on success")
    void deleteSuccess() throws Exception {
        mockMvc.perform(delete("/api/subscriptions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/subscriptions/{id} - should return 404 when not found")
    void deleteNotFound() throws Exception {
        doThrow(new NotFoundException("Assinatura não encontrada com ID: 99"))
                .when(deleteSubscriptionUseCase).execute(99L);

        mockMvc.perform(delete("/api/subscriptions/99"))
                .andExpect(status().isNotFound());
    }
}
