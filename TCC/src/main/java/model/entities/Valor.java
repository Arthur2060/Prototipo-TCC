package model.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.Cobranca;
import model.enums.Metodo;
import model.enums.Periodo;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Valor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Cobranca tipoDeCobranca;
    private Metodo tipoDePagamento;
    private Double preco;
    private Periodo periodo;
}
