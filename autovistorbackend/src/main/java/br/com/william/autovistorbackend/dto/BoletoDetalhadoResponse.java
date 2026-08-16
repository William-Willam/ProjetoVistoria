package br.com.william.autovistorbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BoletoDetalhadoResponse(
        Long idBoleto,
        Long idPagamento,
        String codigoBarras,
        LocalDate dataVencimento,
        BigDecimal valor
) {}