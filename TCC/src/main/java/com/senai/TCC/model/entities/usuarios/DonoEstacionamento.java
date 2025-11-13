package com.senai.TCC.model.entities.usuarios;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.senai.TCC.model.entities.Estacionamento;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@PrimaryKeyJoinColumn(name = "id")
public class DonoEstacionamento extends Usuario {
    @OneToMany(mappedBy = "dono")
    private List<Estacionamento> estacionamentos;
}
