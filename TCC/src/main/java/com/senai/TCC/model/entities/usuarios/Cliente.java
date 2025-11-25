package com.senai.TCC.model.entities.usuarios;

import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Carro;
import com.senai.TCC.model.entities.Reserva;
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
@PrimaryKeyJoinColumn(name = "id")
@SuperBuilder
public class Cliente extends Usuario{
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Carro> Carros;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoes;
}
