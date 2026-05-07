package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.auth.LoginRequestDTO;
import br.com.geloteam.studentmanagement.DTO.auth.LoginResponseDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterRequestDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Models.UserRole;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
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

import java.util.Objects;

@Service
public class AuthService implements UserDetailsService {

    private final CompanyService companyService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,

                       @Lazy AuthenticationManager authenticationManager,
                       CompanyService companyService,
                       TokenService tokenService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.companyService = companyService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: "));
    }

    public LoginResponseDTO login(LoginRequestDTO data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(authenticationToken);

        return tokenService.generateLoginResponse(auth);
    }

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO data) {
        if (userRepository.findUserByEmail(data.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail already exists!");
        }

        if (data.password() == null || data.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty!");
        }

        String encryptedPassword = passwordEncoder.encode(data.password());

        Company company = companyService.findById(data.companyId());

        User user = new User();
        user.setName(data.name());
        user.setEmail(data.email());
        user.setPassword(Objects.requireNonNull(encryptedPassword));
        user.setCompany(company);
        user.setRole(UserRole.USER); // USER is default

        User savedUser = userRepository.save(user);

        return new RegisterResponseDTO(savedUser);
    }

    public RegisterResponseDTO me(Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("User is not authenticated");
        }

        User user = (User) loadUserByUsername(authentication.getName());
        return new RegisterResponseDTO(user);
    }
}