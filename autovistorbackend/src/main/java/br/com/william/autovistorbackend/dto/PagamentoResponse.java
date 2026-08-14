package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Pagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoResponse(
        Long id,
        Pagamento.FormaPagamento formaPagamento,
        Pagamento.StatusPagamento statusPagamento,
        BigDecimal valor,
        LocalDate dataPagamento,
        Long idVistoria
) {}