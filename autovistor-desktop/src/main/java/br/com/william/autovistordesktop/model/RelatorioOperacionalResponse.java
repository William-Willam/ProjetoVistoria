package br.com.william.autovistordesktop.model;

import java.time.LocalDate;
import java.util.Map;

public class RelatorioOperacionalResponse {

    private LocalDate inicio;
    private LocalDate fim;
    private Map<String, Long> agendamentosPorStatus;

    public RelatorioOperacionalResponse() {
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

    public Map<String, Long> getAgendamentosPorStatus() {
        return agendamentosPorStatus;
    }

    public void setAgendamentosPorStatus(Map<String, Long> agendamentosPorStatus) {
        this.agendamentosPorStatus = agendamentosPorStatus;
    }
}