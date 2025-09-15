package com.senai.TCC.application.dto.response;

import com.senai.TCC.model.entities.Avaliacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record AvaliacaoResponse(
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
    public static AvaliacaoResponse fromEntity(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                avaliacao.getCliente().getId(),
                avaliacao.getEstacionamento().getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataDeAvaliacao()
        );
    }
}
