package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.LoginRequestDTO;
import br.com.geloteam.studentmanagement.DTO.LoginResponseDTO;
import br.com.geloteam.studentmanagement.DTO.UserResponseDTO;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@NullMarked
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository,
                       @Lazy AuthenticationManager authenticationManager,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: "));
    }

    public LoginResponseDTO login(LoginRequestDTO data) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = authenticationManager.authenticate(authenticationToken);

        return tokenService.generateLoginResponse(auth);
    }

    public UserResponseDTO me(Authentication authentication) {
        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("User is not authenticated");
        }

        User user = (User) loadUserByUsername(authentication.getName());
        return new UserResponseDTO(user);
    }
}