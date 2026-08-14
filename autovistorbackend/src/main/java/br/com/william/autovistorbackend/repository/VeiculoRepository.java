package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    Optional<Veiculo> findByPlaca(String placa);

    Optional<Veiculo> findByChassi(String chassi);

    boolean existsByPlaca(String placa);

    boolean existsByChassi(String chassi);

    List<Veiculo> findByClienteId(Long idCliente);
}