package br.com.william.autovistordesktop.model;

public class DesignarVistoriadorRequest {

    private Long idFuncionario;

    public DesignarVistoriadorRequest() {
    }

    public DesignarVistoriadorRequest(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public Long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(Long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }
}