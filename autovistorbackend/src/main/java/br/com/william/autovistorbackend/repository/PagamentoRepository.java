package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByVistoriaId(Long idVistoria);

    @Query("""
        SELECT COALESCE(SUM(p.valor), 0) FROM Pagamento p
        WHERE p.statusPagamento = 'PAGO'
        AND p.dataPagamento BETWEEN :inicio AND :fim
    """)
    BigDecimal somarTotalRecebido(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
        SELECT p.formaPagamento AS forma, COALESCE(SUM(p.valor), 0) AS total
        FROM Pagamento p
        WHERE p.statusPagamento = 'PAGO'
        AND p.dataPagamento BETWEEN :inicio AND :fim
        GROUP BY p.formaPagamento
    """)
    List<TotalPorForma> somarPorFormaPagamento(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    interface TotalPorForma {
        Pagamento.FormaPagamento getForma();
        BigDecimal getTotal();
    }
}