package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.ItemVistoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemVistoriaRequest(

        @NotBlank(message = "Nome do item é obrigatório")
        String nomeItem,

        @NotNull(message = "Situação é obrigatória")
        ItemVistoria.Situacao situacao,

        String observacao
) {}