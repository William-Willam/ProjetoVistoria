package br.com.william.autovistorbackend.dto;

import java.time.LocalDateTime;

public record NotaFiscalResponse(
        Long id,
        String numero,
        LocalDateTime dataEmissao,
        Long idPagamento
) {}