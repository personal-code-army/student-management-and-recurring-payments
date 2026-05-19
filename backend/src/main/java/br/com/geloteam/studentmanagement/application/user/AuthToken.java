package br.com.geloteam.studentmanagement.application.user;

public record AuthToken(String accessToken, Long expiresIn) {}
