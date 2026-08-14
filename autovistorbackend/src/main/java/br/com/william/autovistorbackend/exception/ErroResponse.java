package br.com.william.autovistorbackend.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        LocalDateTime timestamp,
        int status,
        String mensagem
) {}