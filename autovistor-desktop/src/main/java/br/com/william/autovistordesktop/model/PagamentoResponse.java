package br.com.william.autovistordesktop.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PagamentoResponse {

    private Long id;
    private String formaPagamento;
    private String statusPagamento;
    private BigDecimal valor;
    private LocalDate dataPagamento;
    private Long idVistoria;

    public PagamentoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public String getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(String statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Long getIdVistoria() {
        return idVistoria;
    }

    public void setIdVistoria(Long idVistoria) {
        this.idVistoria = idVistoria;
    }
}