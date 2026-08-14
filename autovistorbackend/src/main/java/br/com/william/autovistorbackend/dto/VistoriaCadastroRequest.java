package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Vistoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VistoriaCadastroRequest(

        @NotNull(message = "Resultado é obrigatório")
        Vistoria.Resultado resultado,

        @NotBlank(message = "Observações são obrigatórias")
        String observacoes
) {}