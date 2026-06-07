package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.auth.*;
import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Models.UserRole;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import br.com.geloteam.studentmanagement.exception.EntityNotFound;
import br.com.geloteam.studentmanagement.exception.InvalidPasswordException;
import br.com.geloteam.studentmanagement.exception.InvalidTokenException;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    private final CompanyService companyService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final CpfEncryptionService cpfEncryptionService;

    public AuthService(UserRepository userRepository,
                       @Lazy AuthenticationManager authenticationManager,
                       CompanyService companyService,
                       TokenService tokenService,
                       PasswordEncoder passwordEncoder,
                       CpfEncryptionService cpfEncryptionService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.companyService = companyService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.cpfEncryptionService = cpfEncryptionService;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    public LoginResponseDTO login(LoginRequestDTO data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(authenticationToken);
        return tokenService.generateLoginResponse(auth);
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO data) {
        if (userRepository.findUserByEmail(data.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado!");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());
        String encryptedCpf = cpfEncryptionService.encrypt(data.cpf());
        Company company = companyService.findById(data.companyId());

        User user = new User();
        user.setName(data.name());
        user.setEmail(data.email());
        user.setPassword(Objects.requireNonNull(encryptedPassword));
        user.setCpf(encryptedCpf);
        user.setCellphoneNumber(data.cellphoneNumber());
        user.setCompany(company);
        user.setRole(UserRole.USER);

        User savedUser = userRepository.save(user);
        return new RegisterResponseDTO(savedUser);
    }

    public RegisterResponseDTO me(Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Usuário não autenticado");
        }
        User user = (User) loadUserByUsername(authentication.getName());
        return new RegisterResponseDTO(user);
    }

    @Transactional
    public void changePassword(Authentication authentication, ChangePasswordDTO data) {
        User user = (User) loadUserByUsername(authentication.getName());

        if (!passwordEncoder.matches(data.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Senha atual incorreta");
        }

        user.setPassword(passwordEncoder.encode(data.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public String forgotPassword(ForgotPasswordDTO data) {
        User user = userRepository.findByEmail(data.email())
                .orElseThrow(() -> new EntityNotFound("E-mail não encontrado"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        return token;
    }

    @Transactional
    public void resetPassword(ResetPasswordDTO data) {
        User user = userRepository.findByResetToken(data.token())
                .orElseThrow(() -> new InvalidTokenException("Token inválido ou expirado"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token expirado");
        }

        user.setPassword(passwordEncoder.encode(data.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
