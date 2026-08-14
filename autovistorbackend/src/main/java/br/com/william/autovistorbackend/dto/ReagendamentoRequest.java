package br.com.william.autovistorbackend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReagendamentoRequest(
        @NotNull(message = "Nova data é obrigatória")
        @FutureOrPresent(message = "Data não pode ser no passado")
        LocalDate novaData,

        @NotNull(message = "Novo horário é obrigatório")
        LocalTime novaHora
) {}