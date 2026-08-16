package br.com.william.autovistordesktop.model;

public class ItemVistoriaRequest {

    private String nomeItem;
    private String situacao;
    private String observacao;

    public ItemVistoriaRequest() {
    }

    public ItemVistoriaRequest(String nomeItem, String situacao, String observacao) {
        this.nomeItem = nomeItem;
        this.situacao = situacao;
        this.observacao = observacao;
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        this.nomeItem = nomeItem;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}