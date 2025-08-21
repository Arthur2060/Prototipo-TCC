package model.entities.usuarios;

import jakarta.persistence.DiscriminatorValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@DiscriminatorValue("Gerente")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Gerente extends Usuario{
    private String cpfOuCnpj;
}
