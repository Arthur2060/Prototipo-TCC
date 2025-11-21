package com.senai.TCC.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;

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

    @Column(nullable = false, name = "placa_do_carro")
    private String placaDoCarro;

    @Column(name = "hora_de_entrada")
    private LocalTime horaDeEntrada;
    @Column(name = "hora_de_saida")
    private LocalTime horaDeSaida;
    @Column
    private Integer totalHoras;
    @Column(name = "valor_a_pagar")
    private Double valorAPagar;

    private Boolean status;

    public void calcularHorasTotais() {
        this.totalHoras = (int) Duration.between(horaDeEntrada, horaDeSaida).toHours();
    }
}
