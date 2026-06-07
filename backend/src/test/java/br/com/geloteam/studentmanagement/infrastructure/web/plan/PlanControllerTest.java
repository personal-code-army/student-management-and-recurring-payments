package br.com.geloteam.studentmanagement.infrastructure.web.plan;

import br.com.geloteam.studentmanagement.domain.plan.entity.Plan;
import br.com.geloteam.studentmanagement.domain.plan.port.in.DeletePlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.FindPlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.SavePlanUseCase;
import br.com.geloteam.studentmanagement.domain.plan.port.in.UpdatePlanUseCase;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanController.class)
@AutoConfigureMockMvc(addFilters = false)
class PlanControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean SavePlanUseCase savePlanUseCase;
    @MockitoBean UpdatePlanUseCase updatePlanUseCase;
    @MockitoBean DeletePlanUseCase deletePlanUseCase;
    @MockitoBean FindPlanUseCase findPlanUseCase;

    private Plan buildPlan(Long id, String name) {
        Plan p = new Plan();
        p.setId(id);
        p.setName(name);
        p.setMonthlyAmount(99.90);
        p.setFrequency(1);
        return p;
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("POST /api/plans - should return 201 with created plan")
    void createSuccess() throws Exception {
        when(savePlanUseCase.execute(any(Plan.class))).thenReturn(buildPlan(1L, "Básico"));

        mockMvc.perform(post("/api/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Básico","monthlyAmount":99.90,"frequency":1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Básico"))
                .andExpect(jsonPath("$.data.monthlyAmount").value(99.90));
    }

    @Test
    @DisplayName("POST /api/plans - should return 400 when name is missing")
    void createMissingName() throws Exception {
        mockMvc.perform(post("/api/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"monthlyAmount":99.90}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/plans - should return 400 when monthlyAmount is missing")
    void createMissingAmount() throws Exception {
        mockMvc.perform(post("/api/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Básico"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/plans - should return 200 with list")
    void getAllSuccess() throws Exception {
        when(findPlanUseCase.findAll()).thenReturn(List.of(
                buildPlan(1L, "Básico"),
                buildPlan(2L, "Premium")
        ));

        mockMvc.perform(get("/api/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Básico"))
                .andExpect(jsonPath("$.data[1].name").value("Premium"));
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/plans/{id} - should return 200 with plan")
    void getByIdSuccess() throws Exception {
        when(findPlanUseCase.findById(1L)).thenReturn(buildPlan(1L, "Básico"));

        mockMvc.perform(get("/api/plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Básico"));
    }

    @Test
    @DisplayName("GET /api/plans/{id} - should return 404 when not found")
    void getByIdNotFound() throws Exception {
        when(findPlanUseCase.findById(99L))
                .thenThrow(new NotFoundException("Plano não encontrado com ID: 99"));

        mockMvc.perform(get("/api/plans/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("PUT /api/plans/{id} - should return 200 with updated plan")
    void updateSuccess() throws Exception {
        Plan updated = buildPlan(1L, "Premium");
        updated.setMonthlyAmount(199.90);
        when(updatePlanUseCase.execute(eq(1L), any(Plan.class))).thenReturn(updated);

        mockMvc.perform(put("/api/plans/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Premium","monthlyAmount":199.90,"frequency":1}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Premium"))
                .andExpect(jsonPath("$.data.monthlyAmount").value(199.90));
    }

    @Test
    @DisplayName("PUT /api/plans/{id} - should return 404 when plan not found")
    void updateNotFound() throws Exception {
        when(updatePlanUseCase.execute(eq(99L), any(Plan.class)))
                .thenThrow(new NotFoundException("Plano não encontrado com ID: 99"));

        mockMvc.perform(put("/api/plans/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"X","monthlyAmount":10.0}
                                """))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/plans/{id} - should return 200 on success")
    void deleteSuccess() throws Exception {
        mockMvc.perform(delete("/api/plans/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/plans/{id} - should return 404 when plan not found")
    void deleteNotFound() throws Exception {
        doThrow(new NotFoundException("Plano não encontrado com ID: 99"))
                .when(deletePlanUseCase).execute(99L);

        mockMvc.perform(delete("/api/plans/99"))
                .andExpect(status().isNotFound());
    }
}
