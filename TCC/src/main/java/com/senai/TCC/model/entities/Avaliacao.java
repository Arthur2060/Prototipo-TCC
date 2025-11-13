package com.senai.TCC.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.entities.usuarios.Cliente;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "estacionamento_id")
    private Estacionamento estacionamento;

    private Short nota;
    private String comentario;

    @Column(name = "data_avaliacao")
    private LocalDateTime dataDeAvaliacao;

    private Boolean status;
}
