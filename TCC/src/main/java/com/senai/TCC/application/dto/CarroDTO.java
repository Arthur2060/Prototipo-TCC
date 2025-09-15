package com.senai.TCC.application.dto;

import com.senai.TCC.model.entities.Carro;
import io.swagger.v3.oas.annotations.media.Schema;

public record CarroDTO(
        Long id,
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
    public Carro toEntity() {
        Carro carro = new Carro();

        carro.setCor(cor);
        carro.setModelo(modelo);
        carro.setPlaca(placa);

        return carro;
    }

    public static CarroDTO fromEntity(Carro carro) {
        return new CarroDTO(
                carro.getId(),
                carro.getCliente().getId(),
                carro.getPlaca(),
                carro.getModelo(),
                carro.getCor()
        );
    }
}
