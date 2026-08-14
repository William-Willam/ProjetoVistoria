package br.com.william.autovistorbackend.dto;

import jakarta.validation.constraints.NotNull;

public record DesignarVistoriadorRequest(
        @NotNull(message = "Funcionário é obrigatório")
        Long idFuncionario
) {}