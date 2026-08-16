package br.com.william.autovistordesktop.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class RelatorioFinanceiroResponse {

    private LocalDate inicio;
    private LocalDate fim;
    private BigDecimal totalRecebido;
    private Map<String, BigDecimal> totalPorFormaPagamento;

    public RelatorioFinanceiroResponse() {
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public void setFim(LocalDate fim) {
        this.fim = fim;
    }

    public BigDecimal getTotalRecebido() {
        return totalRecebido;
    }

    public void setTotalRecebido(BigDecimal totalRecebido) {
        this.totalRecebido = totalRecebido;
    }

    public Map<String, BigDecimal> getTotalPorFormaPagamento() {
        return totalPorFormaPagamento;
    }

    public void setTotalPorFormaPagamento(Map<String, BigDecimal> totalPorFormaPagamento) {
        this.totalPorFormaPagamento = totalPorFormaPagamento;
    }
}