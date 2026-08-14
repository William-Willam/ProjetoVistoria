package br.com.william.autovistorbackend.dto;

import jakarta.validation.constraints.*;

public record ClienteAtualizacaoRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email
) {}