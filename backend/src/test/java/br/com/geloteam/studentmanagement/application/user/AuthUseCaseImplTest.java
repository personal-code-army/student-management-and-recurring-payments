package br.com.geloteam.studentmanagement.application.user;

import br.com.geloteam.studentmanagement.domain.user.entity.Company;
import br.com.geloteam.studentmanagement.domain.user.entity.User;
import br.com.geloteam.studentmanagement.domain.user.entity.UserRole;
import br.com.geloteam.studentmanagement.domain.user.port.out.CompanyRepositoryPort;
import br.com.geloteam.studentmanagement.domain.user.port.out.UserRepositoryPort;
import br.com.geloteam.studentmanagement.infrastructure.persistence.user.UserJpaEntity;
import br.com.geloteam.studentmanagement.infrastructure.persistence.user.UserJpaRepository;
import br.com.geloteam.studentmanagement.shared.exception.ConflictException;
import br.com.geloteam.studentmanagement.shared.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private CompanyRepositoryPort companyRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthUseCaseImpl authUseCase;

    private final String EMAIL = "vitor@geloteam.com.br";

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Vitor");
        user.setEmail(EMAIL);
        user.setCompanyId(1L);
        return user;
    }

    private UserJpaEntity buildUserJpaEntity() {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(1L);
        entity.setName("Vitor");
        entity.setEmail(EMAIL);
        entity.setPassword("encoded");
        entity.setRole(UserRole.USER);
        return entity;
    }

    @Test
    @DisplayName("Should return UserDetails when user exists")
    void shouldLoadUserByUsernameSuccess() {
        UserJpaEntity mockEntity = buildUserJpaEntity();
        when(userJpaRepository.findUserByEmail(EMAIL)).thenReturn(Optional.of(mockEntity));

        UserDetails result = authUseCase.loadUserByUsername(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getUsername());
        verify(userJpaRepository).findUserByEmail(EMAIL);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userJpaRepository.findUserByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> authUseCase.loadUserByUsername(EMAIL));
    }

    @Test
    @DisplayName("Should return AuthToken when credentials are valid")
    void loginSuccess() {
        Authentication authMock = mock(Authentication.class);
        AuthToken expectedToken = new AuthToken("token-123", 3600L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authMock);
        when(tokenService.generateAuthToken(authMock)).thenReturn(expectedToken);

        AuthToken result = authUseCase.execute(EMAIL, "senha123");

        assertNotNull(result);
        assertEquals("token-123", result.accessToken());
        assertEquals(3600L, result.expiresIn());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals(EMAIL, captor.getValue().getPrincipal());
        assertEquals("senha123", captor.getValue().getCredentials());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when credentials are invalid")
    void loginFailure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authUseCase.execute(EMAIL, "senha-errada"));
        verifyNoInteractions(tokenService);
    }

    @Test
    @DisplayName("Should register user successfully")
    void registerSuccess() {
        Company mockCompany = new Company();
        mockCompany.setId(1L);

        User incoming = new User();
        incoming.setName("Vitor");
        incoming.setEmail("vitor@email.com");

        when(userRepository.findByEmail("vitor@email.com")).thenReturn(Optional.empty());
        when(companyRepository.findById(1L)).thenReturn(Optional.of(mockCompany));
        when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authUseCase.execute(incoming, "senha123", 1L);

        assertNotNull(result);
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("vitor@email.com") &&
                user.getRole() == UserRole.USER &&
                user.getPassword().equals("senha_criptografada")
        ));
    }

    @Test
    @DisplayName("Should throw ConflictException when email already exists on register")
    void registerFailEmailExists() {
        User incoming = new User();
        incoming.setEmail("vitor@email.com");

        when(userRepository.findByEmail("vitor@email.com")).thenReturn(Optional.of(new User()));

        ConflictException exception = assertThrows(ConflictException.class,
                () -> authUseCase.execute(incoming, "123", 1L));

        assertEquals("EMAIL_ALREADY_EXISTS", exception.getCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return current user when email is valid")
    void meSuccess() {
        User mockUser = buildUser();
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockUser));

        User result = authUseCase.execute(EMAIL);

        assertNotNull(result);
        assertEquals(EMAIL, result.getEmail());
        verify(userRepository).findByEmail(EMAIL);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when user is not found on me")
    void meFailUserNotFound() {
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authUseCase.execute(EMAIL));
    }
}
