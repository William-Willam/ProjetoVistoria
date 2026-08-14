package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Funcionario;
import jakarta.validation.constraints.*;

public record FuncionarioCadastroRequest(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Matrícula é obrigatória")
        String matricula,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String senha,

        @NotNull(message = "Cargo é obrigatório")
        Funcionario.Cargo cargo
) {}