package br.com.william.autovistorbackend.dto;

import jakarta.validation.constraints.NotBlank;

public record DesligamentoRequest(
        @NotBlank(message = "Motivo do desligamento é obrigatório")
        String motivo
) {}