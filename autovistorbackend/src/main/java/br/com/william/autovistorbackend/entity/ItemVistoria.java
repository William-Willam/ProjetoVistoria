package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_vistoria")
@Getter
@Setter
@NoArgsConstructor
public class ItemVistoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_vistoria")
    private Long id;

    @Column(name = "nome_item", nullable = false, length = 100)
    private String nomeItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacao", nullable = false)
    private Situacao situacao;

    @Column(name = "observacao")
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vistoria", nullable = false)
    private Vistoria vistoria;

    public enum Situacao {
        OK, AVARIA
    }
}