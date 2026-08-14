package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.LancamentoCaixa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;

public interface LancamentoCaixaRepository extends JpaRepository<LancamentoCaixa, Long> {
}