package br.com.william.autovistorbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record RelatorioFinanceiroResponse(
        LocalDate inicio,
        LocalDate fim,
        BigDecimal totalRecebido,
        Map<String, BigDecimal> totalPorFormaPagamento
) {}