package com.senai.TCC.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Time;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Acesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;

    @ManyToOne
    @JoinColumn(name = "carro_id")
    private Carro carro;

    @Column(nullable = false)
    private String placaDoCarro;

    @Column
    private Time horaDeEntrada;
    @Column
    private Time horaDeSaida;
    @Column
    private Integer totalHoras;
    @Column
    private Double valorAPagar;

    private Boolean status;

    public void calcularHorasTotais() {
        this.totalHoras = Integer.parseInt(
                String.valueOf(
                        this.horaDeEntrada.getTime()
                        + this.horaDeSaida.getTime())
        );
    }
}
