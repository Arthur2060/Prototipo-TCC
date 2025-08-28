package com.senai.TCC.application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import com.senai.TCC.model.entities.Acesso;

import java.sql.Time;

public record AcessoDTO(
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
                examples = "17:50"
        )
        Time horaDeEntrada,
        @Schema(
                name = "horaDeSaida",
                description = "Hora em que o carro saiu",
                examples = "19:30"
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
    public Acesso fromDTO() {
        Acesso acesso = new Acesso();

        acesso.setPlacaDoCarro(placaDoCarro);
        acesso.setHoraDeEntrada(horaDeEntrada);
        acesso.setHoraDeSaida(horaDeSaida);
        acesso.calcularHorasTotais();
        acesso.setValorAPagar(valorAPagar);

        return acesso;
    }

    public static AcessoDTO toDTO(Acesso acesso) {
        return new AcessoDTO(
                acesso.getPlacaDoCarro(),
                acesso.getHoraDeEntrada(),
                acesso.getHoraDeSaida(),
                acesso.getValorAPagar(),
                acesso.getEstacionamento().getId()
        );
    }
}
