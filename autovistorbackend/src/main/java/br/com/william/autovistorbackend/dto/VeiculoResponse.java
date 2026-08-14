package br.com.william.autovistorbackend.dto;

public record VeiculoResponse(
        Long id,
        String placa,
        String tipoVeiculo,
        String nomeVeiculo,
        String modelo,
        Integer anoVeiculo,
        String chassi,
        String observacoes,
        Long idCliente
) {}