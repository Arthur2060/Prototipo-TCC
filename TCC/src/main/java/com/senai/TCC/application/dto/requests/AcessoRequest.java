package com.senai.TCC.application.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Time;
import java.time.LocalTime;

public record AcessoRequest(
        @Schema(
                name = "carroId",
                description = "Id do carro que acessou o estacionamento",
                examples = "1"
        )
        Long carroId,
        @Schema(
                name = "placaDoCarro",
                description = "Placa do carro que acessou o estacionamento, caso erro," +
                        " resulta em Null e necessita preenchimento manual posteriormente.",
                examples = "EUD8679"
        )
        String placaDoCarro,
        @Schema(
                name = "horaDeEntrada",
                description = "Hora em que o carro entrou",
                examples = "17:50:00",
                type = "string",
                pattern = "HH:mm:ss"
        )
        LocalTime horaDeEntrada,
        @Schema(
                name = "horaDeSaida",
                description = "Hora em que o carro saiu",
                examples = "19:30:00",
                type = "string",
                pattern = "HH:mm:ss"
        )
        LocalTime horaDeSaida,
        @Schema(
                name = "valorAPagar",
                description = "Valor total a pagar pelo tempo de uso do estacionamento",
                examples = "15.00"
        )
        Double valorAPagar,

        @Schema(
                name = "estacioId",
                description = "Id do estacionamento em que o veiculo entrou",
                examples = "3"
        )
        Long estacioId
) {
}
