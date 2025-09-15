package com.senai.TCC.application.dto.create_requests;

import com.senai.TCC.model.entities.Acesso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Time;

public record AcessoCreateRequest(
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
    public Acesso toEntity() {
        Acesso acesso = new Acesso();

        acesso.setPlacaDoCarro(placaDoCarro);
        acesso.setHoraDeEntrada(horaDeEntrada);
        acesso.setValorAPagar(valorAPagar);
        acesso.setHoraDeSaida(horaDeSaida);
        acesso.calcularHorasTotais();

        return acesso;
    }
}
