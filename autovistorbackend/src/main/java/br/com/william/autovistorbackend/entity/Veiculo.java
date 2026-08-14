package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "veiculo")
@Getter
@Setter
@NoArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veiculo")
    private Long id;

    @Column(name = "placa", nullable = false, unique = true, length = 8)
    private String placa;

    @Column(name = "tipo_veiculo", nullable = false, length = 20)
    private String tipoVeiculo;

    @Column(name = "nome_veiculo", nullable = false, length = 100)
    private String nomeVeiculo;

    @Column(name = "modelo", nullable = false, length = 100)
    private String modelo;

    @Column(name = "ano_veiculo", nullable = false)
    private Integer anoVeiculo;

    @Column(name = "chassi", nullable = false, unique = true, length = 17)
    private String chassi;

    @Column(name = "observacoes")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
}