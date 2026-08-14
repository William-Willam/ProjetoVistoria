package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Agendamento;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponse(
        Long id,
        LocalDate dataAgendamento,
        LocalTime hora,
        Agendamento.TipoVistoria tipoVistoria,
        Agendamento.StatusAgendamento statusAgendamento,
        Long idCliente,
        Long idVeiculo,
        Long idFuncionario
) {}