package br.com.william.autovistorbackend.dto;

import java.time.LocalDateTime;

public record FotoVistoriaResponse(
        Long id,
        String caminhoArquivo,
        String descricao,
        LocalDateTime dataUpload
) {}