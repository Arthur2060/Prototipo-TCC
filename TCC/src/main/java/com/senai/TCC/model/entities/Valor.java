package com.senai.TCC.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.enums.Cobranca;
import com.senai.TCC.model.enums.Metodo;
import com.senai.TCC.model.enums.Periodo;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Valor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Cobranca tipoDeCobranca;
    private Metodo tipoDePagamento;
    private Double preco;
    private Periodo periodo;

    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;
}
