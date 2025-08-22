package application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import model.entities.Valor;
import model.enums.Cobranca;
import model.enums.Metodo;
import model.enums.Periodo;

public record ValorDTO(
        @Schema(
                name = "Tipo de cobrança",
                description = "Tipo de cobrança do estacionamento, definido por enum Cobranca",
                examples = "FIXO"
        )
        Cobranca tipoDeCobranca,
        @Schema(
                name = "Tipo de pagamento",
                description = "Tipo de pagamento do estacionamento, definido por enum Metodo",
                examples = "DINHEIRO"
        )
        Metodo tipoDePagamento,
        @Schema(
                name = "Preço",
                description = "Preço cobrado pelo estacionamento",
                examples = "10.00"
        )
        Double preco,
        @Schema(
                name = "Período",
                description = "Período de cobrança do estacionamento, definido por enum Periodo",
                examples = "MANHA"
        )
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
