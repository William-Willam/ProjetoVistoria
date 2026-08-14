package br.com.william.autovistorbackend.dto;

import jakarta.validation.constraints.*;

import java.time.Year;

public record VeiculoCadastroRequest(

        @NotBlank(message = "Placa é obrigatória")
        String placa,

        @NotBlank(message = "Tipo de veículo é obrigatório")
        String tipoVeiculo,

        @NotBlank(message = "Nome do veículo é obrigatório")
        String nomeVeiculo,

        @NotBlank(message = "Modelo é obrigatório")
        String modelo,

        @NotNull(message = "Ano é obrigatório")
        @Min(value = 1900, message = "Ano inválido")
        Integer anoVeiculo,

        @NotBlank(message = "Chassi é obrigatório")
        @Size(min = 17, max = 17, message = "Chassi deve ter 17 caracteres")
        String chassi,

        String observacoes,

        @NotNull(message = "Cliente é obrigatório")
        Long idCliente
) {}