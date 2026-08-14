package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Optional<Boleto> findByPagamentoId(Long idPagamento);
}