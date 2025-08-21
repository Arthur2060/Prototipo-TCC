package model.entities.usuarios;

import jakarta.persistence.DiscriminatorValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.entities.Estacionamento;

import java.util.List;

@DiscriminatorValue("DONO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonoEstacionamento extends Usuario {
    private List<Estacionamento> estacionamentos;
    private List<String> numerosDeEscrituras;
}
