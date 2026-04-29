package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.auth.LoginRequestDTO;
import br.com.geloteam.studentmanagement.DTO.auth.LoginResponseDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterRequestDTO;
import br.com.geloteam.studentmanagement.DTO.auth.RegisterResponseDTO;
import br.com.geloteam.studentmanagement.Models.Company;
import br.com.geloteam.studentmanagement.Models.User;
import br.com.geloteam.studentmanagement.Models.UserRole;
import br.com.geloteam.studentmanagement.Repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyService companyService;
    @Mock
    private TokenService tokenService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private final String EMAIL = "vitor@geloteam.com.br";

    @Test
    @DisplayName("Should return UserDetails when user exists")
    void shouldLoadUserByUsernameSuccess() {
        User mockUser = new User();
        mockUser.setEmail(EMAIL);
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(mockUser));

        UserDetails result = authService.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getUsername());
        verify(userRepository, times(1)).findUserByEmail(EMAIL);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(EMAIL);
        });
    }

    @Test
    @DisplayName("Should return LoginResponseDTO when credentials are valid")
    void loginSuccess() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("vitor@email.com", "senha123");
        Authentication authMock = mock(Authentication.class);
        LoginResponseDTO expectedResponse = new LoginResponseDTO("token-123", 3600L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        when(tokenService.generateLoginResponse(authMock)).thenReturn(expectedResponse);

        LoginResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("token-123", response.accessToken());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());

        assertEquals("vitor@email.com", captor.getValue().getPrincipal());
        assertEquals("senha123", captor.getValue().getCredentials());
    }

    @Test
    @DisplayName("Should throw exception when credentials are invalid")
    void loginFailure() {
        LoginRequestDTO loginRequest = new LoginRequestDTO("vitor@email.com", "senha-errada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid user or password"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        verifyNoInteractions(tokenService);
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerSuccess() {
        RegisterRequestDTO request = new RegisterRequestDTO("Vitor", "vitor@email.com", "senha123", "11999999999", 1L);
        Company mockCompany = new Company();
        mockCompany.setId(1L);

        when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");
        when(companyService.findById(1L)).thenReturn(mockCompany);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponseDTO response = authService.register(request);

        assertNotNull(response);
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("vitor@email.com") &&
                        user.getRole() == UserRole.USER &&
                        user.getPassword().equals("senha_criptografada")
        ));
    }

    @Test
    @DisplayName("Should throw CONFLICT when email already exists")
    void registerFailEmailExists() {
        RegisterRequestDTO request = new RegisterRequestDTO("Vitor", "vitor@email.com", "123", "11999999999", 1L);
        when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.of(new User()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.register(request);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("E-mail already exists!", exception.getReason());
    }

    @Test
    @DisplayName("Should return current user details when authenticated")
    void meSuccess() {
        Company mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setName("Gelo Team");

        User mockUser = new User();
        mockUser.setEmail("vitor@email.com");
        mockUser.setName("Vitor");
        mockUser.setCompany(mockCompany);

        Authentication authMock = mock(Authentication.class);
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authMock.getName()).thenReturn("vitor@email.com");

        when(userRepository.findUserByEmail("vitor@email.com")).thenReturn(Optional.of(mockUser));

        RegisterResponseDTO response = authService.me(authMock);

        assertNotNull(response);
        assertEquals("vitor@email.com", response.email());
        verify(userRepository).findUserByEmail("vitor@email.com");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when not authenticated")
    void meFailNotAuthenticated() {
        Authentication authMock = mock(Authentication.class);
        when(authMock.isAuthenticated()).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> {
            authService.me(authMock);
        });
    }
}
