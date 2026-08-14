package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Funcionario;

public record FuncionarioResponse(
        Long id,
        String nome,
        String email,
        String matricula,
        Funcionario.Cargo cargo,
        boolean ativo
) {}