package com.senai.TCC.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.enums.Cobranca;
import com.senai.TCC.model.enums.Metodo;
import com.senai.TCC.model.enums.Periodo;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Valor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_cobranca")
    private Cobranca tipoDeCobranca;

    @Column(name = "tipo_pagamento")
    private Metodo tipoDePagamento;
    private Double preco;

    @Column(name = "periodo")
    private Periodo periodo;

    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;
}
