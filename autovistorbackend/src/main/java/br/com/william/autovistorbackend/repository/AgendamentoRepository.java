package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByClienteId(Long idCliente);

    List<Agendamento> findByFuncionarioId(Long idFuncionario);

    Optional<Agendamento> findByIdAndClienteId(Long id, Long idCliente);

    boolean existsByDataAgendamentoAndHoraAndFuncionarioIdAndStatusAgendamentoNotIn(
            LocalDate dataAgendamento,
            LocalTime hora,
            Long idFuncionario,
            List<Agendamento.StatusAgendamento> statusExcluidos
    );

    long countByStatusAgendamentoAndDataAgendamentoBetween(
            Agendamento.StatusAgendamento status, LocalDate inicio, LocalDate fim);

    List<Agendamento> findByStatusAgendamento(Agendamento.StatusAgendamento status);
}