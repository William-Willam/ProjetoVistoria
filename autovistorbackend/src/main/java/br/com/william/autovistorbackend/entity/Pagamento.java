package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagamento")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pagamento", nullable = false)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;

    @Column(name = "valor", nullable = false)
    private BigDecimal valor;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vistoria", nullable = false, unique = true)
    private Vistoria vistoria;

    public enum FormaPagamento {
        DEBITO, CREDITO, PIX, BOLETO, DINHEIRO
    }

    public enum StatusPagamento {
        PENDENTE, PAGO, RECUSADO
    }
}