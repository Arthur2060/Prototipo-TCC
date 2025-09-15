package com.senai.TCC.application.dto.create_requests;

import com.senai.TCC.model.entities.Valor;
import com.senai.TCC.model.enums.Cobranca;
import com.senai.TCC.model.enums.Metodo;
import com.senai.TCC.model.enums.Periodo;
import io.swagger.v3.oas.annotations.media.Schema;

public record ValorCreateRequest(
        @Schema(
                name = "tipoDeCobranca",
                description = "Tipo de cobrança do estacionamento, definido por enum Cobranca",
                examples = "PORHORA"
        )
        Cobranca tipoDeCobranca,
        @Schema(
                name = "tipoDePagamento",
                description = "Tipo de pagamento do estacionamento, definido por enum Metodo",
                examples = "DINHEIRO"
        )
        Metodo tipoDePagamento,
        @Schema(
                name = "preco",
                description = "Preço cobrado pelo estacionamento",
                examples = "10.00"
        )
        Double preco,
        @Schema(
                name = "periodo",
                description = "Período de cobrança do estacionamento, definido por enum Periodo",
                examples = "FINALDESEMANA"
        )
        Periodo periodo,

        @Schema(
                name = "estacioId",
                description = "ID do estacionamento associado a esse valor",
                examples = "1"
        )
        Long estacioId
) {
    public Valor toEntity() {
        Valor valor = new Valor();

        valor.setPreco(preco);
        valor.setPeriodo(periodo);
        valor.setTipoDeCobranca(tipoDeCobranca);
        valor.setTipoDePagamento(tipoDePagamento);

        return valor;
    }
}
