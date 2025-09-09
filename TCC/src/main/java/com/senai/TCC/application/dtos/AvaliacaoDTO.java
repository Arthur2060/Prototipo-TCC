package com.senai.TCC.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import com.senai.TCC.model.entities.Avaliacao;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AvaliacaoDTO(
        Long id,
        @Schema
        (
                name = "clienteId",
                description = "ID do cliente que fez a avaliação",
                examples = "1"
        )
        Long clienteId,
        @Schema(
                name = "estacioId",
                description = "ID do estacionamento que foi avaliado",
                examples = "1"
        )
        Long estacioId,
        @Schema(
                name = "nota",
                description = "Nota dada pelo cliente ao estacionamento, de 0 a 5",
                examples = "3"
        )
        Short nota,
        @Schema(
                name = "comentario",
                description = "Comentario feito pelo cliente sobre o estacionamento",
                examples = "Estacionamento bom, porem um pouco caro"
        )
        String comentario,
        @Schema(
                name = "dataDeAvaliacao",
                description = "Data em que a avaliação foi feita",
                examples = "2023-11-25T15:30:00"
        )
        LocalDateTime dataDeAvaliacao
) {
    public Avaliacao toEntity() {
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setDataDeAvaliacao(dataDeAvaliacao);
        avaliacao.setComentario(comentario);
        avaliacao.setNota(nota);

        return avaliacao;
    }

    public static AvaliacaoDTO fromEntity(Avaliacao avaliacao) {
        return new AvaliacaoDTO(
                avaliacao.getId(),
                avaliacao.getCliente().getId(),
                avaliacao.getEstacionamento().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataDeAvaliacao()
        );
    }
}
