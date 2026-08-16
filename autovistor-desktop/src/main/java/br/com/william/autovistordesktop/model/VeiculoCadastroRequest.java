package br.com.william.autovistordesktop.model;

public class VeiculoCadastroRequest {

    private String placa;
    private String tipoVeiculo;
    private String nomeVeiculo;
    private String modelo;
    private Integer anoVeiculo;
    private String chassi;
    private String observacoes;
    private Long idCliente;

    public VeiculoCadastroRequest() {
    }

    public VeiculoCadastroRequest(String placa, String tipoVeiculo, String nomeVeiculo, String modelo,
                                  Integer anoVeiculo, String chassi, String observacoes, Long idCliente) {
        this.placa = placa;
        this.tipoVeiculo = tipoVeiculo;
        this.nomeVeiculo = nomeVeiculo;
        this.modelo = modelo;
        this.anoVeiculo = anoVeiculo;
        this.chassi = chassi;
        this.observacoes = observacoes;
        this.idCliente = idCliente;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getTipoVeiculo() {
        return tipoVeiculo;
    }

    public void setTipoVeiculo(String tipoVeiculo) {
        this.tipoVeiculo = tipoVeiculo;
    }

    public String getNomeVeiculo() {
        return nomeVeiculo;
    }

    public void setNomeVeiculo(String nomeVeiculo) {
        this.nomeVeiculo = nomeVeiculo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAnoVeiculo() {
        return anoVeiculo;
    }

    public void setAnoVeiculo(Integer anoVeiculo) {
        this.anoVeiculo = anoVeiculo;
    }

    public String getChassi() {
        return chassi;
    }

    public void setChassi(String chassi) {
        this.chassi = chassi;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }
}