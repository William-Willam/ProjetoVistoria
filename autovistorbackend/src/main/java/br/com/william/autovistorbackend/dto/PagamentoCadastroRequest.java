package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Pagamento;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record PagamentoCadastroRequest(

        @NotNull(message = "Forma de pagamento é obrigatória")
        Pagamento.FormaPagamento formaPagamento,

        @NotNull(message = "Valor é obrigatório")
        @Positive(message = "Valor deve ser maior que zero")
        BigDecimal valor
) {}