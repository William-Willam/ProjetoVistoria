package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vistoria")
@Getter
@Setter
@NoArgsConstructor
public class Vistoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vistoria")
    private Long id;

    @Column(name = "data_vistoria", nullable = false)
    private LocalDate dataVistoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "resultado", nullable = false)
    private Resultado resultado;

    @Column(name = "observacoes")
    private String observacoes;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agendamento", nullable = false, unique = true)
    private Agendamento agendamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario", nullable = false)
    private Funcionario funcionario;

    @OneToMany(mappedBy = "vistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVistoria> itens = new ArrayList<>();

    @OneToMany(mappedBy = "vistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoVistoria> fotos = new ArrayList<>();

    public enum Resultado {
        APROVADO,
        REPROVADO,
        APROVADO_COM_RESSALVAS
    }
}