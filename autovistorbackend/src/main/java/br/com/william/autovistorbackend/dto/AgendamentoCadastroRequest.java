package br.com.william.autovistorbackend.dto;

import br.com.william.autovistorbackend.entity.Agendamento;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoCadastroRequest(

        @NotNull(message = "Data é obrigatória")
        @FutureOrPresent(message = "Data não pode ser no passado")
        LocalDate dataAgendamento,

        @NotNull(message = "Horário é obrigatório")
        LocalTime hora,

        @NotNull(message = "Tipo de vistoria é obrigatório")
        Agendamento.TipoVistoria tipoVistoria,

        @NotNull(message = "Veículo é obrigatório")
        Long idVeiculo
) {}