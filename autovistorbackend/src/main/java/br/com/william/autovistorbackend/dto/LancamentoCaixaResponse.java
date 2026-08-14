package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.LancamentoCaixa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LancamentoCaixaResponse(
        Long id,
        LancamentoCaixa.Tipo tipo,
        BigDecimal valor,
        String descricao,
        LocalDateTime dataLancamento,
        Long idPagamento
) {}