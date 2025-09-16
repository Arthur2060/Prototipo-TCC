package com.senai.TCC.application.dto.response;

import com.senai.TCC.model.entities.Acesso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Time;

public record AcessoResponse(
        Long id,
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
                examples = "17:50",
                type = "string",
                pattern = "HH:mm:ss"
        )
        Time horaDeEntrada,
        @Schema(
                name = "horaDeSaida",
                description = "Hora em que o carro saiu",
                examples = "19:30",
                type = "string",
                pattern = "HH:mm:ss"
        )
        Time horaDeSaida,
        @Schema(
                name = "totalHoras",
                description = "Total de horas que o carro ficou no estacionamento",
                examples = "1.5"
        )
        Double totalHoras,
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
        Long estacioId,
        @Schema(
                name = "status",
                description = "Status da entidade, se está ativa ou inativa no sistema",
                examples = "true"
        )
        Boolean status
) {
}
