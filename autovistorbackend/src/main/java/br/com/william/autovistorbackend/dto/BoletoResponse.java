package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Boleto;

import java.time.LocalDate;

public record BoletoResponse(
        Long id,
        String codigoBarras,
        LocalDate dataVencimento,
        Boleto.Status status,
        Long idPagamento
) {}