package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "nota_fiscal")
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nota_fiscal")
    private Long id;

    @Column(name = "numero", nullable = false, unique = true, length = 30)
    private String numero;

    @Column(name = "data_emissao", insertable = false, updatable = false)
    private LocalDateTime dataEmissao;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagamento", nullable = false, unique = true)
    private Pagamento pagamento;
}