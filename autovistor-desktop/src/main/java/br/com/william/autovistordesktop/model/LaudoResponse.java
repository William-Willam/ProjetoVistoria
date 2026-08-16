package br.com.william.autovistordesktop.model;

import java.time.LocalDateTime;

public class LaudoResponse {

    private Long id;
    private String caminhoArquivo;
    private LocalDateTime dataGeracao;
    private Long idVistoria;

    public LaudoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Long getIdVistoria() {
        return idVistoria;
    }

    public void setIdVistoria(Long idVistoria) {
        this.idVistoria = idVistoria;
    }
}