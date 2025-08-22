package com.senai.TCC.model.entities.usuarios;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
}
