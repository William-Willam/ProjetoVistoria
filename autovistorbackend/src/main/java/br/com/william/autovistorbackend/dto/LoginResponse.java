package br.com.william.autovistorbackend.dto;

public record LoginResponse(
        String token,
        String tipo,
        String role
) {}