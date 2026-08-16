package br.com.william.autovistordesktop.model;

import java.time.LocalDate;
import java.util.List;

public class VistoriaResponse {

    private Long id;
    private LocalDate dataVistoria;
    private String resultado;
    private String observacoes;
    private Long idAgendamento;
    private Long idFuncionario;
    private List<ItemVistoriaResponse> itens;
    private List<FotoVistoriaResponse> fotos;

    public VistoriaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataVistoria() {
        return dataVistoria;
    }

    public void setDataVistoria(LocalDate dataVistoria) {
        this.dataVistoria = dataVistoria;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getIdAgendamento() {
        return idAgendamento;
    }

    public void setIdAgendamento(Long idAgendamento) {
        this.idAgendamento = idAgendamento;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public List<ItemVistoriaResponse> getItens() {
        return itens;
    }

    public void setItens(List<ItemVistoriaResponse> itens) {
        this.itens = itens;
    }

    public List<FotoVistoriaResponse> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoVistoriaResponse> fotos) {
        this.fotos = fotos;
    }
}