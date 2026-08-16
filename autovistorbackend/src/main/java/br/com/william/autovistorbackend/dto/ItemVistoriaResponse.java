package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.ItemVistoria;

public record ItemVistoriaResponse(
        Long id,
        String nomeItem,
        ItemVistoria.Situacao situacao,
        String observacao
) {}