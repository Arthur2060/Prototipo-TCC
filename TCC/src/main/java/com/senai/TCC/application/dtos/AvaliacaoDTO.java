package com.senai.TCC.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import com.senai.TCC.model.entities.Avaliacao;

import java.time.LocalDateTime;

public record AvaliacaoDTO(
        @Schema(
                name = "Nota",
                description = "Nota dada pelo cliente ao estacionamento, de 0 a 5",
                examples = "3"
        )
        Short nota,
        @Schema(
                name = "Comentario",
                description = "Comentario feito pelo cliente sobre o estacionamento",
                examples = "Estacionamento bom, porem um pouco caro"
        )
        String comentario,
        @Schema(
                name = "Data de avaliação",
                description = "Data em que a avaliação foi feita",
                examples = "2023-11-25T15:30:00"
        )
        LocalDateTime dataDeAvaliacao
) {
    public Avaliacao fromDTO() {
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setDataDeAvaliacao(dataDeAvaliacao);
        avaliacao.setComentario(comentario);
        avaliacao.setNota(nota);

        return avaliacao;
    }

    public static AvaliacaoDTO toDTO(Avaliacao avaliacao) {
        return new AvaliacaoDTO(
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataDeAvaliacao()
        );
    }
}
