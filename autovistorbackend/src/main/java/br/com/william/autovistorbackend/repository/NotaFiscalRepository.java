package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {
    boolean existsByPagamentoId(Long idPagamento);
}