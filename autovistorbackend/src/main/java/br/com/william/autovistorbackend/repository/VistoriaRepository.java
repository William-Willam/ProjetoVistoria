package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.Vistoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VistoriaRepository extends JpaRepository<Vistoria, Long> {

    Optional<Vistoria> findByAgendamentoId(Long idAgendamento);

    boolean existsByAgendamentoId(Long idAgendamento);
}