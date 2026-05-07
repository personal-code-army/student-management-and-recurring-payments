package br.com.geloteam.studentmanagement.Controllers;

import br.com.geloteam.studentmanagement.DTO.auth.LoginRequestDTO;
import br.com.geloteam.studentmanagement.DTO.auth.LoginResponseDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterRequestDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO data) {
        return ResponseEntity.ok(authService.login(data));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody @Valid RegisterRequestDTO data) {
        RegisterResponseDTO newUser = authService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/me")
    public ResponseEntity<RegisterResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication));
    }
}
