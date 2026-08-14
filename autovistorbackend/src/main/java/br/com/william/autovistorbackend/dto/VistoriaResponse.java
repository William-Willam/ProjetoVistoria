package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Vistoria;

import java.time.LocalDate;

public record VistoriaResponse(
        Long id,
        LocalDate dataVistoria,
        Vistoria.Resultado resultado,
        String observacoes,
        Long idAgendamento,
        Long idFuncionario
) {}