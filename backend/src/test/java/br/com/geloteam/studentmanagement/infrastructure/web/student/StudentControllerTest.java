package br.com.geloteam.studentmanagement.infrastructure.web.student;

import br.com.geloteam.studentmanagement.domain.student.entity.Student;
import br.com.geloteam.studentmanagement.domain.student.port.in.CreateStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.in.DeleteStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.in.FindStudentUseCase;
import br.com.geloteam.studentmanagement.domain.student.port.in.UpdateStudentUseCase;
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

@WebMvcTest(StudentController.class)
@AutoConfigureMockMvc(addFilters = false)
class StudentControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean CreateStudentUseCase createStudentUseCase;
    @MockitoBean UpdateStudentUseCase updateStudentUseCase;
    @MockitoBean DeleteStudentUseCase deleteStudentUseCase;
    @MockitoBean FindStudentUseCase findStudentUseCase;

    private Student buildStudent(Long id, String name, String cpf) {
        Student s = new Student();
        s.setId(id);
        s.setName(name);
        s.setCpf(cpf);
        s.setBirthDate(LocalDate.of(2000, 1, 15));
        s.setActive(true);
        s.setPlanId(1L);
        return s;
    }

    private String studentJson(String name, String cpf) {
        return """
                {
                  "name":"%s",
                  "cpf":"%s",
                  "birthDate":"2000-01-15",
                  "active":true,
                  "planId":1
                }
                """.formatted(name, cpf);
    }

    // ==================== CREATE ====================

    @Test
    @DisplayName("POST /api/students - should return 201 with created student")
    void createSuccess() throws Exception {
        when(createStudentUseCase.execute(any(Student.class)))
                .thenReturn(buildStudent(1L, "João Silva", "11144477735"));

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson("João Silva", "11144477735")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("João Silva"))
                .andExpect(jsonPath("$.data.cpf").value("11144477735"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("POST /api/students - should return 400 when name is missing")
    void createMissingName() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cpf":"11144477735","birthDate":"2000-01-15","active":true}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/students - should return 400 when birthDate is missing")
    void createMissingBirthDate() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"João","cpf":"11144477735","active":true}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/students - should return 200 with all students")
    void getAllSuccess() throws Exception {
        when(findStudentUseCase.findAll()).thenReturn(List.of(
                buildStudent(1L, "João", "11144477735"),
                buildStudent(2L, "Maria", "22233366635")
        ));

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("João"))
                .andExpect(jsonPath("$.data[1].name").value("Maria"));
    }

    @Test
    @DisplayName("GET /api/students?name=João - should filter by name")
    void getAllFilterByName() throws Exception {
        when(findStudentUseCase.findByName("João"))
                .thenReturn(List.of(buildStudent(1L, "João", "11144477735")));

        mockMvc.perform(get("/api/students").param("name", "João"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("João"));
    }

    @Test
    @DisplayName("GET /api/students?active=true - should filter by active status")
    void getAllFilterByActive() throws Exception {
        when(findStudentUseCase.findByActive(true))
                .thenReturn(List.of(buildStudent(1L, "Ativo", "11144477735")));

        mockMvc.perform(get("/api/students").param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].active").value(true));
    }

    @Test
    @DisplayName("GET /api/students?minAge=18&maxAge=30 - should filter by age range")
    void getAllFilterByAgeRange() throws Exception {
        when(findStudentUseCase.findByAgeRange(18, 30))
                .thenReturn(List.of(buildStudent(1L, "Jovem", "11144477735")));

        mockMvc.perform(get("/api/students").param("minAge", "18").param("maxAge", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/students/{id} - should return 200 with student")
    void getByIdSuccess() throws Exception {
        when(findStudentUseCase.findById(1L)).thenReturn(buildStudent(1L, "João", "11144477735"));

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("João"));
    }

    @Test
    @DisplayName("GET /api/students/{id} - should return 404 when not found")
    void getByIdNotFound() throws Exception {
        when(findStudentUseCase.findById(99L))
                .thenThrow(new NotFoundException("Aluno não encontrado com ID: 99"));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("PUT /api/students/{id} - should return 200 with updated student")
    void updateSuccess() throws Exception {
        Student updated = buildStudent(1L, "João Atualizado", "11144477735");
        when(updateStudentUseCase.execute(eq(1L), any(Student.class))).thenReturn(updated);

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson("João Atualizado", "11144477735")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("João Atualizado"));
    }

    @Test
    @DisplayName("PUT /api/students/{id} - should return 404 when not found")
    void updateNotFound() throws Exception {
        when(updateStudentUseCase.execute(eq(99L), any(Student.class)))
                .thenThrow(new NotFoundException("Aluno não encontrado com ID: 99"));

        mockMvc.perform(put("/api/students/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson("X", "11144477735")))
                .andExpect(status().isNotFound());
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/students/{id} - should return 200 on success")
    void deleteSuccess() throws Exception {
        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.success").value(true));
    }

    @Test
    @DisplayName("DELETE /api/students/{id} - should return 404 when not found")
    void deleteNotFound() throws Exception {
        doThrow(new NotFoundException("Aluno não encontrado com ID: 99"))
                .when(deleteStudentUseCase).execute(99L);

        mockMvc.perform(delete("/api/students/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== SUBSCRIPTIONS ====================

    @Test
    @DisplayName("GET /api/students/{id}/subscriptions - should return 200 with subscriptions")
    void getSubscriptionsSuccess() throws Exception {
        when(findStudentUseCase.findSubscriptionsByStudent(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/students/1/subscriptions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
