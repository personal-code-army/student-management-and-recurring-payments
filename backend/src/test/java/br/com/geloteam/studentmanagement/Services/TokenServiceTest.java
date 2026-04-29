package br.com.geloteam.studentmanagement.Services;

import br.com.geloteam.studentmanagement.DTO.auth.LoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        lenient().when(jwtEncoder.encode(any())).thenReturn(jwt);
        lenient().when(jwt.getTokenValue()).thenReturn("mocked-jwt-token");
    }

    @Test
    @DisplayName("Should generate token with correct claims and formatted scopes")
    void shouldGenerateTokenCorrectly() {
        String username = "usuario.teste";
        var authorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        );

        when(authentication.getName()).thenReturn(username);
        doReturn(authorities).when(authentication).getAuthorities();

        String token = tokenService.generateToken(authentication);

        assertEquals("mocked-jwt-token", token);

        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals("geloteam-api", claims.getClaim("iss"));
        assertEquals(username, claims.getSubject());
        assertEquals("ADMIN USER", claims.getClaim("scope"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());
    }

    @Test
    @DisplayName("Should return LoginResponseDTO with token and duration")
    void shouldGenerateLoginResponse() {
        when(authentication.getName()).thenReturn("user");
        doReturn(List.of()).when(authentication).getAuthorities();

        LoginResponseDTO response = tokenService.generateLoginResponse(authentication);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.accessToken());
        assertEquals(3600L, response.expiresIn());
    }

    @Test
    @DisplayName("Should throw RuntimeException when encoding fails")
    void shouldThrowExceptionWhenEncodingFails() {
        when(authentication.getName()).thenReturn("user");
        doReturn(List.of()).when(authentication).getAuthorities();
        when(jwtEncoder.encode(any())).thenThrow(new JwtEncodingException("Encoding error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenService.generateToken(authentication);
        });

        assertTrue(exception.getMessage().contains("Error generating JWT token"));
        assertInstanceOf(JwtEncodingException.class, exception.getCause());
    }
}