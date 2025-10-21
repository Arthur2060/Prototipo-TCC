package com.senai.TCC.model.entities;

import com.senai.TCC.model.entities.usuarios.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Time;
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

    private String placaDoCarro;
    private Time horaDeEntrada;
    private Time horaDeSaida;
    private Integer totalHoras;
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
