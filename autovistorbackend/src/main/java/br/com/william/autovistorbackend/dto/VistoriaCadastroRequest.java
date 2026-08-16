package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Vistoria;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VistoriaCadastroRequest(

        @NotNull(message = "Resultado é obrigatório")
        Vistoria.Resultado resultado,

        @NotBlank(message = "Observações são obrigatórias")
        String observacoes,

        @NotEmpty(message = "Informe ao menos um item do checklist")
        @Valid
        List<ItemVistoriaRequest> itens
) {}