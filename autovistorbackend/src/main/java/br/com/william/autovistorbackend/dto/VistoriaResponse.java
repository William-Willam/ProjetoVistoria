package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Vistoria;

import java.time.LocalDate;
import java.util.List;

public record VistoriaResponse(
        Long id,
        LocalDate dataVistoria,
        Vistoria.Resultado resultado,
        String observacoes,
        Long idAgendamento,
        Long idFuncionario,
        List<ItemVistoriaResponse> itens,
        List<FotoVistoriaResponse> fotos
) {}