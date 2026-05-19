package br.com.geloteam.studentmanagement.application.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    public static final long EXPIRY_SECONDS = 3600L;
    private static final String ISSUER = "geloteam-api";

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public AuthToken generateAuthToken(Authentication auth) {
        return new AuthToken(generateToken(auth), EXPIRY_SECONDS);
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = extractScopes(authentication);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(EXPIRY_SECONDS))
                .subject(authentication.getName())
                .claim("scope", scope)
                .build();

        var parameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);

        try {
            return jwtEncoder.encode(parameters).getTokenValue();
        } catch (JwtEncodingException e) {
            log.error("Failed to generate JWT token for user '{}'", authentication.getName(), e);
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    private String extractScopes(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.joining(" "));
    }
}
