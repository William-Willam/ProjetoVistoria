package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "laudo")
@Getter
@Setter
@NoArgsConstructor
public class Laudo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_laudo")
    private Long id;

    @Column(name = "caminho_arquivo", nullable = false, length = 255)
    private String caminhoArquivo;

    @Column(name = "data_geracao", insertable = false, updatable = false)
    private LocalDateTime dataGeracao;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vistoria", nullable = false, unique = true)
    private Vistoria vistoria;
}