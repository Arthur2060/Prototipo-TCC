package com.senai.TCC.model.entities;

import com.senai.TCC.model.entities.usuarios.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Carro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "carro", cascade = CascadeType.ALL)
    private List<Acesso> acessos;

    @Column(nullable = false, unique = true, length = 7)
    private String placa;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String cor;

    private Boolean status;
}
