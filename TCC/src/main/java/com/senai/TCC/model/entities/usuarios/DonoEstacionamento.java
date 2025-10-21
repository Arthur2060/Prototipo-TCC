package com.senai.TCC.model.entities.usuarios;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.entities.Estacionamento;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@DiscriminatorValue("DONO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DonoEstacionamento extends Usuario {
    @OneToMany
    private List<Estacionamento> estacionamentos;
}
