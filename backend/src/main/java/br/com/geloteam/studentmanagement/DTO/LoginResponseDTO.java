package br.com.geloteam.studentmanagement.DTO;

public record LoginResponseDTO(
        String accessToken,
        Long expiresIn
) {
}
