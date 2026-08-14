package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "agendamento")
@Getter
@Setter
@NoArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agendamento")
    private Long id;

    @Column(name = "data_agendamento", nullable = false)
    private LocalDate dataAgendamento;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vistoria", nullable = false)
    private TipoVistoria tipoVistoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_agendamento", nullable = false)
    private StatusAgendamento statusAgendamento = StatusAgendamento.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veiculo", nullable = false)
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario")
    private Funcionario funcionario;

    public enum TipoVistoria {
        TRANSFERENCIA,
        CAUTELAR,
        PREVIA
    }

    public enum StatusAgendamento {
        PENDENTE,
        CONFIRMADO,
        CONCLUIDO,
        CANCELADO,
        REAGENDADO
    }
}