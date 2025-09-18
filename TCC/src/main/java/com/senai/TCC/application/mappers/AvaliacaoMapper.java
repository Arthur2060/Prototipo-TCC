package com.senai.TCC.application.mappers;

import com.senai.TCC.application.dto.requests.AvaliacaoRequest;
import com.senai.TCC.application.dto.response.AvaliacaoResponse;
import com.senai.TCC.model.entities.Avaliacao;

public class AvaliacaoMapper {

    public static Avaliacao toEntity(AvaliacaoRequest dto) {
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setDataDeAvaliacao(dto.dataDeAvaliacao());
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        return avaliacao;
    }


    public static AvaliacaoResponse fromEntity(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getCliente().getId(),
                avaliacao.getEstacionamento().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataDeAvaliacao(),
                avaliacao.getStatus()
        );
    }
}
