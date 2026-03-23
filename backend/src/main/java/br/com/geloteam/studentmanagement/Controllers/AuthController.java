package br.com.geloteam.studentmanagement.Controllers;

import br.com.geloteam.studentmanagement.DTO.LoginRequestDTO;
import br.com.geloteam.studentmanagement.DTO.LoginResponseDTO;
import br.com.geloteam.studentmanagement.DTO.UserResponseDTO;
import br.com.geloteam.studentmanagement.Services.AuthService;
import jakarta.validation.Valid;
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

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.me(authentication));
    }
}
