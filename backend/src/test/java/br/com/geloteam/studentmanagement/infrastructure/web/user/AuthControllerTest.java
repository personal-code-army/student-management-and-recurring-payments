package br.com.geloteam.studentmanagement.infrastructure.web.user;

import br.com.geloteam.studentmanagement.application.user.AuthToken;
import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.entity.UserRole;
import br.com.geloteam.studentmanagement.domain.user.port.in.ForgotPasswordUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.LoginUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.MeUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.RegisterUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.ResetPasswordUseCase;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import br.com.geloteam.studentmanagement.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean LoginUseCase loginUseCase;
    @MockitoBean RegisterUseCase registerUseCase;
    @MockitoBean MeUseCase meUseCase;
    @MockitoBean ForgotPasswordUseCase forgotPasswordUseCase;
    @MockitoBean ResetPasswordUseCase resetPasswordUseCase;

    private User buildUser(Long id, String email, UserRole role) {
        User u = new User();
        u.setId(id);
        u.setName("Test User");
        u.setEmail(email);
        u.setCompanyId(1L);
        u.setRole(role);
        return u;
    }

    // ==================== LOGIN ====================

    @Test
    @DisplayName("POST /api/auth/login - should return 200 with token")
    void loginSuccess() throws Exception {
        when(loginUseCase.execute("user@test.com", "Senha@123"))
                .thenReturn(new AuthToken("jwt-token", 3600L));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@test.com","password":"Senha@123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600));
    }

    @Test
    @DisplayName("POST /api/auth/login - should return 401 on bad credentials")
    void loginBadCredentials() throws Exception {
        when(loginUseCase.execute(anyString(), anyString()))
                .thenThrow(new BadCredentialsException("bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@test.com","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTH_INVALID_CREDENTIALS"));
    }

    @Test
    @DisplayName("POST /api/auth/login - should return 400 when email is missing")
    void loginMissingEmail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"password":"Senha@123"}
                                """))
                .andExpect(status().isBadRequest());
    }

    // ==================== REGISTER ====================

    @Test
    @DisplayName("POST /api/auth/register - should return 201 with user data")
    void registerSuccess() throws Exception {
        User saved = buildUser(1L, "novo@test.com", UserRole.USER);
        when(registerUseCase.execute(any(User.class), anyString(), any(Long.class))).thenReturn(saved);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Novo User",
                                  "email":"novo@test.com",
                                  "password":"Senha@123",
                                  "cpf":"11144477735",
                                  "companyId":1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("novo@test.com"))
                .andExpect(jsonPath("$.data.role").value("USER"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 409 when email already exists")
    void registerEmailConflict() throws Exception {
        when(registerUseCase.execute(any(User.class), anyString(), any(Long.class)))
                .thenThrow(new ConflictException("EMAIL_ALREADY_EXISTS", "E-mail já cadastrado!"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"User",
                                  "email":"existente@test.com",
                                  "password":"Senha@123",
                                  "cpf":"11144477735",
                                  "companyId":1
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 400 on invalid password")
    void registerInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"User",
                                  "email":"user@test.com",
                                  "password":"fraca",
                                  "cpf":"11144477735",
                                  "companyId":1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("POST /api/auth/register - should return 400 on invalid CPF")
    void registerInvalidCpf() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"User",
                                  "email":"user@test.com",
                                  "password":"Senha@123",
                                  "cpf":"11111111111",
                                  "companyId":1
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ==================== ME ====================

    @Test
    @DisplayName("GET /api/auth/me - should return 200 with current user")
    void meSuccess() throws Exception {
        User user = buildUser(1L, "admin@test.com", UserRole.ADMIN);
        when(meUseCase.execute("admin@test.com")).thenReturn(user);

        var principal = new UsernamePasswordAuthenticationToken(
                "admin@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(get("/api/auth/me").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("admin@test.com"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    @DisplayName("GET /api/auth/me - should return 401 when user not found")
    void meUserNotFound() throws Exception {
        when(meUseCase.execute(anyString()))
                .thenThrow(new UnauthorizedException("Usuário não encontrado"));

        var principal = new UsernamePasswordAuthenticationToken(
                "ghost@test.com", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(get("/api/auth/me").principal(principal))
                .andExpect(status().isUnauthorized());
    }

    // ==================== FORGOT PASSWORD ====================

    @Test
    @DisplayName("POST /api/auth/forgot-password - should return 202 with generic message")
    void forgotPasswordSuccess() throws Exception {
        when(forgotPasswordUseCase.generateResetToken("user@test.com")).thenReturn("reset-uuid-token");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@test.com"}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.token").doesNotExist())
                .andExpect(jsonPath("$.data.message").exists());
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password - should return 202 when email not found (no enumeration)")
    void forgotPasswordNotFound() throws Exception {
        when(forgotPasswordUseCase.generateResetToken(anyString())).thenReturn("");

        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ghost@test.com"}
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.token").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/forgot-password - should return 400 on invalid email")
    void forgotPasswordInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    // ==================== RESET PASSWORD ====================

    @Test
    @DisplayName("POST /api/auth/reset-password - should return 200 on success")
    void resetPasswordSuccess() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"valid-token","newPassword":"NovaSenha@123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/reset-password - should return 404 on invalid token")
    void resetPasswordInvalidToken() throws Exception {
        doThrow(new NotFoundException("Token inválido ou expirado"))
                .when(resetPasswordUseCase).resetPassword(anyString(), anyString());

        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"bad-token","newPassword":"NovaSenha@123"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/auth/reset-password - should return 400 on weak password")
    void resetPasswordWeakPassword() throws Exception {
        mockMvc.perform(post("/api/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"token":"valid-token","newPassword":"fraca"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}
