package com.senai.TCC.application.mappers;

import com.senai.TCC.application.dto.create_requests.ValorCreateRequest;
import com.senai.TCC.application.dto.response.ValorResponse;
import com.senai.TCC.model.entities.Valor;

public class ValorMapper {

    public static Valor toEntity(ValorCreateRequest dto) {
        Valor valor = new Valor();

        valor.setPreco(dto.preco());
        valor.setPeriodo(dto.periodo());
        valor.setTipoDeCobranca(dto.tipoDeCobranca());
        valor.setTipoDePagamento(dto.tipoDePagamento());

        return valor;
    }

    public static ValorResponse fromEntity(Valor valor) {
        return new ValorResponse(
                valor.getId(),
                valor.getTipoDeCobranca(),
                valor.getTipoDePagamento(),
                valor.getPreco(),
                valor.getPeriodo(),
                valor.getEstacionamento().getId()
        );
    }
}
