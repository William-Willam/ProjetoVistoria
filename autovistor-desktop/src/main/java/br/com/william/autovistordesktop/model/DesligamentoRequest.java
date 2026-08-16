package br.com.william.autovistordesktop.model;

public class DesligamentoRequest {

    private String motivo;

    public DesligamentoRequest() {
    }

    public DesligamentoRequest(String motivo) {
        this.motivo = motivo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}