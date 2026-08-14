package br.com.william.autovistorbackend.repository;

import br.com.william.autovistorbackend.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByMatricula(String matricula);

    boolean existsByMatricula(String matricula);

    boolean existsByEmail(String email);

    Optional<Funcionario> findByEmail(String email);
}