package br.com.william.autovistorbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "boleto")
@Getter
@Setter
@NoArgsConstructor
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleto")
    private Long id;

    @Column(name = "codigo_barras", nullable = false, length = 60)
    private String codigoBarras;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.EMITIDO;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pagamento", nullable = false, unique = true)
    private Pagamento pagamento;

    public enum Status {
        EMITIDO, PAGO, VENCIDO
    }
}