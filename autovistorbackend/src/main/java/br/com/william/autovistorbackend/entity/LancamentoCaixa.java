package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lancamento_caixa")
@Getter
@Setter
@NoArgsConstructor
public class LancamentoCaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lancamento")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private Tipo tipo;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "data_lancamento", insertable = false, updatable = false)
    private LocalDateTime dataLancamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagamento")
    private Pagamento pagamento;

    public enum Tipo {
        ENTRADA, SAIDA
    }
}