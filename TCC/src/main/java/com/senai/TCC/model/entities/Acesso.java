package com.senai.TCC.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Acesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;

    private String placaDoCarro;
    private Time horaDeEntrada;
    private Time horaDeSaida;
    private Integer totalHoras;
    private Double valorAPagar;

    public void calcularHorasTotais() {
        this.totalHoras = Integer.parseInt(
                String.valueOf(
                        this.horaDeEntrada.getTime()
                        + this.horaDeSaida.getTime())
        );
    }
}
