package br.com.geloteam.studentmanagement.DTO.auth;

public record LoginResponseDTO(
        String accessToken,
        Long expiresIn
) {
}
