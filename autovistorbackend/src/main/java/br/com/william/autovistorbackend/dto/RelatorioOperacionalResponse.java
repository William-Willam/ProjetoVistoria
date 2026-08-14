package br.com.william.autovistorbackend.dto;

import java.time.LocalDate;
import java.util.Map;

public record RelatorioOperacionalResponse(
        LocalDate inicio,
        LocalDate fim,
        Map<String, Long> agendamentosPorStatus
) {}