package com.senai.TCC.model.entities;

import com.senai.TCC.model.enums.StatusReserva;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.entities.usuarios.Cliente;
import lombok.experimental.SuperBuilder;

import java.sql.Time;
import java.util.Date;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;

    @Column(name = "data_reserva")
    private Date dataDaReserva;

    @Column(name = "hora_reserva")
    private Time horaDaReserva;

    private StatusReserva statusReserva;

    private Boolean status;
}
