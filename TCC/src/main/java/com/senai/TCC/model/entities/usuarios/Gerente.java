package com.senai.TCC.model.entities.usuarios;

import com.senai.TCC.model.entities.Estacionamento;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("GERENTE")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Gerente extends Usuario{
    private String cpfOuCnpj;

    @OneToMany
    private Estacionamento estacionamento;
}
