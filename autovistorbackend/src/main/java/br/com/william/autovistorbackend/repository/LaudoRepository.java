package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.Laudo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LaudoRepository extends JpaRepository<Laudo, Long> {
    Optional<Laudo> findByVistoriaId(Long idVistoria);
}