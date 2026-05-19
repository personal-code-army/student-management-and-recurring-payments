package br.com.geloteam.studentmanagement.infrastructure.web.user.dto;

public record LoginResponse(String accessToken, Long expiresIn) {}
