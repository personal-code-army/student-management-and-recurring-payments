package br.com.geloteam.studentmanagement.application.user;

import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.entity.UserRole;
import br.com.geloteam.studentmanagement.domain.user.port.in.ForgotPasswordUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.LoginUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.MeUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.RegisterUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.in.ResetPasswordUseCase;
import br.com.geloteam.studentmanagement.domain.user.port.out.CompanyRepositoryPort;
import br.com.geloteam.studentmanagement.domain.user.port.out.UserRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.persistence.user.UserJpaRepository;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.NotFoundException;
import br.com.geloteam.studentmanagement.shared.exception.UnauthorizedException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
public class AuthUseCaseImpl implements LoginUseCase, RegisterUseCase, MeUseCase, ForgotPasswordUseCase, ResetPasswordUseCase, UserDetailsService {

    private static String sha256(String input) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "<invalid>";
        return "***@" + email.substring(email.indexOf('@') + 1);
    }

    private final UserRepositoryPort userRepository;
    private final CompanyRepositoryPort companyRepository;
    private final UserJpaRepository userJpaRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final CpfEncryptionService cpfEncryptionService;

    public AuthUseCaseImpl(UserRepositoryPort userRepository,
                           CompanyRepositoryPort companyRepository,
                           UserJpaRepository userJpaRepository,
                           @Lazy AuthenticationManager authenticationManager,
                           TokenService tokenService,
                           PasswordEncoder passwordEncoder,
                           CpfEncryptionService cpfEncryptionService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.userJpaRepository = userJpaRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.cpfEncryptionService = cpfEncryptionService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userJpaRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found with email '{}'", maskEmail(email));
                    return new UsernameNotFoundException("User not found with email: " + maskEmail(email));
                });
    }

    @Override
    public AuthToken execute(String email, String password) {
        var token = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManager.authenticate(token);
        log.info("Login successful: {}", maskEmail(email));
        return tokenService.generateAuthToken(auth);
    }

    @Override
    @Transactional
    public User execute(User user, String rawPassword, Long companyId) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("Register failed: email already in use '{}'", maskEmail(user.getEmail()));
            throw new ConflictException("EMAIL_ALREADY_EXISTS", "E-mail já cadastrado!");
        }

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company não encontrada com ID: " + companyId));

        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setCompanyId(company.getId());
        user.setRole(UserRole.USER);

        if (user.getCpf() != null && !user.getCpf().isBlank()) {
            user.setCpf(cpfEncryptionService.encrypt(user.getCpf()));
        }

        User saved = userRepository.save(user);
        log.info("New user registered: {}", maskEmail(saved.getEmail()));
        return saved;
    }

    @Override
    public User execute(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
    }

    @Override
    @Transactional
    public String generateResetToken(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            log.info("Password reset requested for non-existing account: {}", maskEmail(email));
            return "";
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(sha256(token));
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        log.info("Reset token generated for user: {}", user.getId());
        return token;
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(sha256(token))
                .orElseThrow(() -> new NotFoundException("Token inválido ou expirado"));

        if (user.getResetTokenExpiry() == null || LocalDateTime.now().isAfter(user.getResetTokenExpiry())) {
            throw new UnauthorizedException("Token expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
        log.info("Password reset for user: {}", user.getId());
    }
}
