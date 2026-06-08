package br.com.geloteam.studentmanagement.infrastructure.web.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.entity.UserRole;
import br.com.geloteam.studentmanagement.domain.user.port.in.ChangePasswordUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.DeleteUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserByIdUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.FindUserUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.UpdateUserUseCase;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import br.com.geloteam.studentmanagement.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean FindUserUseCase findUserUseCase;
    @MockitoBean FindUserByIdUseCase findUserByIdUseCase;
    @MockitoBean UpdateUserUseCase updateUserUseCase;
    @MockitoBean DeleteUserUseCase deleteUserUseCase;
    @MockitoBean ChangePasswordUseCase changePasswordUseCase;

    private User buildUser(Long id, String email, UserRole role) {
        User u = new User();
        u.setId(id);
        u.setName("Test User");
        u.setEmail(email);
        u.setCompanyId(1L);
        u.setRole(role);
        u.setCellphoneNumber("11999999999");
        return u;
    }

    // ==================== GET ALL ====================

    @Test
    @DisplayName("GET /api/users - should return 200 with list")
    void getAllSuccess() throws Exception {
        when(findUserUseCase.findAll()).thenReturn(List.of(
                buildUser(1L, "a@test.com", UserRole.ADMIN),
                buildUser(2L, "b@test.com", UserRole.USER)
        ));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].email").value("a@test.com"))
                .andExpect(jsonPath("$.data[1].email").value("b@test.com"));
    }

    @Test
    @DisplayName("GET /api/users - should return 200 with empty list")
    void getAllEmpty() throws Exception {
        when(findUserUseCase.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // ==================== GET BY ID ====================

    @Test
    @DisplayName("GET /api/users/{id} - should return 200 with user")
    void getByIdSuccess() throws Exception {
        when(findUserByIdUseCase.findById(1L)).thenReturn(buildUser(1L, "u@test.com", UserRole.USER));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("u@test.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - should return 404 when not found")
    void getByIdNotFound() throws Exception {
        when(findUserByIdUseCase.findById(99L))
                .thenThrow(new NotFoundException("Usuário não encontrado com ID: 99"));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("ENTITY_NOT_FOUND"));
    }

    // ==================== UPDATE ====================

    @Test
    @DisplayName("PUT /api/users/{id} - should return 200 with updated user")
    void updateSuccess() throws Exception {
        User updated = buildUser(1L, "updated@test.com", UserRole.USER);
        updated.setName("Updated Name");
        when(updateUserUseCase.execute(eq(1L), anyString(), anyString(), anyString(), anyLong()))
                .thenReturn(updated);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Updated Name",
                                  "email":"updated@test.com",
                                  "cellphoneNumber":"11999999999",
                                  "companyId":1
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Name"))
                .andExpect(jsonPath("$.data.email").value("updated@test.com"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - should return 404 when user not found")
    void updateNotFound() throws Exception {
        when(updateUserUseCase.execute(eq(99L), anyString(), anyString(), anyString(), anyLong()))
                .thenThrow(new NotFoundException("Usuário não encontrado com ID: 99"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"X","email":"x@t.com","cellphoneNumber":"11999999999","companyId":1}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - should return 409 when email already taken")
    void updateEmailConflict() throws Exception {
        when(updateUserUseCase.execute(eq(1L), anyString(), anyString(), anyString(), anyLong()))
                .thenThrow(new ConflictException("EMAIL_ALREADY_EXISTS", "E-mail já cadastrado!"));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"X","email":"taken@t.com","cellphoneNumber":"11999999999","companyId":1}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - should return 400 when name is blank")
    void updateMissingName() throws Exception {
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"x@t.com","companyId":1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ==================== DELETE ====================

    @Test
    @DisplayName("DELETE /api/users/{id} - should return 200 with deleted user")
    void deleteSuccess() throws Exception {
        when(deleteUserUseCase.execute(1L)).thenReturn(buildUser(1L, "d@test.com", UserRole.USER));

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - should return 404 when user not found")
    void deleteNotFound() throws Exception {
        when(deleteUserUseCase.execute(99L))
                .thenThrow(new NotFoundException("Usuário não encontrado com ID: 99"));

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    // ==================== CHANGE PASSWORD ====================

    @Test
    @DisplayName("PATCH /api/users/change-password - should return 200 on success")
    void changePasswordSuccess() throws Exception {
        var principal = new UsernamePasswordAuthenticationToken(
                "user@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/api/users/change-password")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"Atual@123","newPassword":"Nova@123"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PATCH /api/users/change-password - should return 401 on wrong current password")
    void changePasswordWrongCurrent() throws Exception {
        doThrow(new UnauthorizedException("Senha atual incorreta"))
                .when(changePasswordUseCase).execute(anyString(), anyString(), anyString());

        var principal = new UsernamePasswordAuthenticationToken(
                "user@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/api/users/change-password")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"Errada@123","newPassword":"Nova@123"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/users/change-password - should return 400 on weak new password")
    void changePasswordWeakNew() throws Exception {
        var principal = new UsernamePasswordAuthenticationToken(
                "user@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(patch("/api/users/change-password")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"Atual@123","newPassword":"fraca"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
