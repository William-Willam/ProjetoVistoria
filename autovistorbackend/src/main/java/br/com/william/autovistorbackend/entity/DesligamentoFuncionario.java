package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "desligamento_funcionario")
@Getter
@Setter
@NoArgsConstructor
public class DesligamentoFuncionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_funcionario")
    private Long idFuncionario;

    @Column(name = "nome_funcionario", nullable = false, length = 150)
    private String nomeFuncionario;

    @Column(name = "matricula", nullable = false, length = 20)
    private String matricula;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @Column(name = "data_desligamento", insertable = false, updatable = false)
    private LocalDateTime dataDesligamento;
}