package br.com.william.autovistordesktop.model;

import java.util.List;

public class VistoriaCadastroRequest {

    private String resultado;
    private String observacoes;
    private List<ItemVistoriaRequest> itens;

    public VistoriaCadastroRequest() {
    }

    public VistoriaCadastroRequest(String resultado, String observacoes, List<ItemVistoriaRequest> itens) {
        this.resultado = resultado;
        this.observacoes = observacoes;
        this.itens = itens;
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

    public List<ItemVistoriaRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemVistoriaRequest> itens) {
        this.itens = itens;
    }
}