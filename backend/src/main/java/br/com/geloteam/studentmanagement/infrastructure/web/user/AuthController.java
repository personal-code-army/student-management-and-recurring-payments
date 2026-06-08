package br.com.geloteam.studentmanagement.infrastructure.web.user;

import br.com.geloteam.studentmanagement.application.user.AuthToken;
import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.port.in.ForgotPasswordUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.LoginUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.MeUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.RegisterUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.ResetPasswordUseCase;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.ForgotPasswordRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.LoginRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.LoginResponse;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.RegisterRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.ResetPasswordRequest;
import br.com.geloteam.studentmanagement.infrastructure.web.user.dto.UserResponse;
import br.com.geloteam.studentmanagement.shared.web.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final MeUseCase meUseCase;
    private final ForgotPasswordUseCase forgotPasswordUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    public AuthController(LoginUseCase loginUseCase,
                          RegisterUseCase registerUseCase,
                          MeUseCase meUseCase,
                          ForgotPasswordUseCase forgotPasswordUseCase,
                          ResetPasswordUseCase resetPasswordUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
        this.meUseCase = meUseCase;
        this.forgotPasswordUseCase = forgotPasswordUseCase;
        this.resetPasswordUseCase = resetPasswordUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        AuthToken token = loginUseCase.execute(request.email(), request.password());
        LoginResponse response = new LoginResponse(token.accessToken(), token.expiresIn());
        return ResponseEntity.ok(ApiResponse.data(response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@RequestBody @Valid RegisterRequest request) {
        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setCpf(request.cpf());
        user.setCellphoneNumber(request.cellphoneNumber());

        User saved = registerUseCase.execute(user, request.password(), request.companyId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.data(UserResponse.from(saved)));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        User user = meUseCase.execute(authentication.getName());
        return ResponseEntity.ok(ApiResponse.data(UserResponse.from(user)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Map<String, String>>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        forgotPasswordUseCase.generateResetToken(request.email());
        return ResponseEntity.accepted().body(
                ApiResponse.data(Map.of("message", "Se o e-mail existir, um link de redefinição será enviado."))
        );
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        resetPasswordUseCase.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok(ApiResponse.data(null));
    }
}
