package com.senai.TCC.model.entities;

import com.senai.TCC.model.entities.usuarios.Cliente;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Carro {
    @Id
    private Long id;

    @ManyToOne
    private Cliente cliente;

    private String placa;
    private String modelo;
    private String cor;
}
