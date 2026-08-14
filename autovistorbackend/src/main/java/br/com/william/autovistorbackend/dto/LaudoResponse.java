package br.com.william.autovistorbackend.dto;

import java.time.LocalDateTime;

public record LaudoResponse(
        Long id,
        String caminhoArquivo,
        LocalDateTime dataGeracao,
        Long idVistoria
) {}