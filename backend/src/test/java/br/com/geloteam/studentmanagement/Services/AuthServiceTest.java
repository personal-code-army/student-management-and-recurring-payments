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
        // Arrange
        User mockUser = new User();
        mockUser.setEmail(EMAIL);
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(mockUser));

        // Act
        UserDetails result = authService.loadUserByUsername(EMAIL);

        // Assert
        assertNotNull(result);
        assertEquals(EMAIL, result.getUsername());
        verify(userRepository, times(1)).findUserByEmail(EMAIL);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            authService.loadUserByUsername(EMAIL);
        });
    }

    @Test
    @DisplayName("Should return LoginResponseDTO when credentials are valid")
    void loginSuccess() {
        // Arrange (Preparação)
        LoginRequestDTO loginRequest = new LoginRequestDTO("vitor@email.com", "senha123");
        Authentication authMock = mock(Authentication.class);
        LoginResponseDTO expectedResponse = new LoginResponseDTO("token-123", 3600L);

        // Quando o manager for chamado com qualquer token, retorna o nosso authMock
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);

        // Quando o tokenService receber esse authMock, retorna o DTO esperado
        when(tokenService.generateLoginResponse(authMock)).thenReturn(expectedResponse);

        // Act (Ação)
        LoginResponseDTO response = authService.login(loginRequest);

        // Assert (Verificação)
        assertNotNull(response);
        assertEquals("token-123", response.accessToken());

        // Verifica se o manager foi chamado exatamente com os dados do DTO
        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());

        assertEquals("vitor@email.com", captor.getValue().getPrincipal());
        assertEquals("senha123", captor.getValue().getCredentials());
    }

    @Test
    @DisplayName("Should throw exception when credentials are invalid")
    void loginFailure() {
        // Arrange
        LoginRequestDTO loginRequest = new LoginRequestDTO("vitor@email.com", "senha-errada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid user or password"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });

        // Garante que, se a senha deu erro, o gerador de token NUNCA foi chamado
        verifyNoInteractions(tokenService);
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerSuccess() {
        // Arrange
        RegisterRequestDTO request = new RegisterRequestDTO("Vitor", "vitor@email.com", "senha123", "11999999999", 1L);
        Company mockCompany = new Company();
        mockCompany.setId(1L);

        when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");
        when(companyService.findById(1L)).thenReturn(mockCompany);

        // Simula o save retornando o próprio objeto que recebeu
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        RegisterResponseDTO response = authService.register(request);

        // Assert
        assertNotNull(response);
        verify(passwordEncoder).encode("senha123"); // Garante que criptografou a senha certa
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("vitor@email.com") &&
                        user.getRole() == UserRole.USER &&
                        user.getPassword().equals("senha_criptografada")
        ));
    }

    @Test
    @DisplayName("Should throw CONFLICT when email already exists")
    void registerFailEmailExists() {
        // Arrange
        RegisterRequestDTO request = new RegisterRequestDTO("Vitor", "vitor@email.com", "123", "11999999999", 1L);
        when(userRepository.findUserByEmail(request.email())).thenReturn(Optional.of(new User()));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authService.register(request);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("E-mail already exists!", exception.getReason());
    }

    @Test
    @DisplayName("Should return current user details when authenticated")
    void meSuccess() {
        // 1. Criar a empresa para evitar o NullPointerException
        Company mockCompany = new Company();
        mockCompany.setId(1L);
        mockCompany.setName("Gelo Team");

        // 2. Criar o usuário e ASSOCIAR a empresa
        User mockUser = new User();
        mockUser.setEmail("vitor@email.com");
        mockUser.setName("Vitor");
        mockUser.setCompany(mockCompany); // <--- O PULO DO GATO ESTÁ AQUI

        Authentication authMock = mock(Authentication.class);
        when(authMock.isAuthenticated()).thenReturn(true);
        when(authMock.getName()).thenReturn("vitor@email.com");

        // Mock do repositório retornando o usuário com empresa
        when(userRepository.findUserByEmail("vitor@email.com")).thenReturn(Optional.of(mockUser));

        // Act
        RegisterResponseDTO response = authService.me(authMock);

        // Assert
        assertNotNull(response);
        assertEquals("vitor@email.com", response.email());
        verify(userRepository).findUserByEmail("vitor@email.com");
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when not authenticated")
    void meFailNotAuthenticated() {
        // Arrange
        Authentication authMock = mock(Authentication.class);
        when(authMock.isAuthenticated()).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authService.me(authMock);
        });
    }
}
