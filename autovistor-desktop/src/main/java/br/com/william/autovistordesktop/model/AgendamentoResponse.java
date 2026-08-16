package br.com.william.autovistordesktop.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class AgendamentoResponse {

    private Long id;
    private LocalDate dataAgendamento;
    private LocalTime hora;
    private String tipoVistoria;
    private String statusAgendamento;
    private Long idCliente;
    private Long idVeiculo;
    private Long idFuncionario;

    public AgendamentoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getTipoVistoria() {
        return tipoVistoria;
    }

    public void setTipoVistoria(String tipoVistoria) {
        this.tipoVistoria = tipoVistoria;
    }

    public String getStatusAgendamento() {
        return statusAgendamento;
    }

    public void setStatusAgendamento(String statusAgendamento) {
        this.statusAgendamento = statusAgendamento;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }
}