package com.senai.TCC.application.dto.requests.create_requests;

import io.swagger.v3.oas.annotations.media.Schema;

public record CarroCreateRequest(
        @Schema(
                name = "clienteId",
                example = "3",
                description = "ID do usuario proprietário do carro"
        )
        Long clienteId,

        @Schema(
                name = "placa",
                example = "EUD-8679",
                description = "Placa de identificação do carro"
        )
        String placa,

        @Schema(
                name = "modelo",
                example = "Corsa",
                description = "Modelo do carro"
        )
        String modelo,

        @Schema(
                name = "cor",
                example = "Fuxia",
                description = "Cor do carro"
        )
        String cor
) {
}
