package application.dtos;

import model.entities.Valor;
import model.enums.Cobranca;
import model.enums.Metodo;
import model.enums.Periodo;

public record ValorDTO(
        Cobranca tipoDeCobranca,
        Metodo tipoDePagamento,
        Double preco,
        Periodo periodo
) {
    public Valor fromDTO() {
        Valor valor = new Valor();

        valor.setPreco(preco);
        valor.setPeriodo(periodo);
        valor.setTipoDeCobranca(tipoDeCobranca);
        valor.setTipoDePagamento(tipoDePagamento);

        return valor;
    }

    public static ValorDTO toDTO(Valor valor) {
        return new ValorDTO(
                valor.getTipoDeCobranca(),
                valor.getTipoDePagamento(),
                valor.getPreco(),
                valor.getPeriodo()
        );
    }
}
