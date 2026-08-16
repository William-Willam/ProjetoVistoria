package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "foto_vistoria")
@Getter
@Setter
@NoArgsConstructor
public class FotoVistoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto_vistoria")
    private Long id;

    @Column(name = "caminho_arquivo", nullable = false, length = 255)
    private String caminhoArquivo;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "data_upload", insertable = false, updatable = false)
    private LocalDateTime dataUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vistoria", nullable = false)
    private Vistoria vistoria;
}