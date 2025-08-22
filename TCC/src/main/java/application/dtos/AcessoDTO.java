package application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import model.entities.Acesso;

import java.sql.Time;

public record AcessoDTO(
        @Schema(
                name = "Placa do carro",
                description = "Placa do carro que acessou o estacionamento, caso erro, resulta em Null",
                examples = "EUD8679"
        )
        String placaDoCarro,
        @Schema(
                name = "Horario de entrada",
                description = "Hora em o carro entrou",
                examples = "17:50"
        )
        Time horaDeEntrada,
        @Schema(
                name = "Horario de saída",
                description = "Hora em que o carro saiu",
                examples = "19:30"
        )
        Time horaDeSaida,
        @Schema(
                name = "Valor a pagar",
                description = "Valor total a pagar pelo tempo de uso do estacionamento",
                examples = "15.00"
        )
        Double valorAPagar
) {
    public Acesso fromDTO() {
        Acesso acesso = new Acesso();

        acesso.setPlacaDoCarro(placaDoCarro);
        acesso.setHoraDeEntrada(horaDeEntrada);
        acesso.setHoraDeSaida(horaDeSaida);
        acesso.setTotalHoras(
                Integer.parseInt(
                        String.valueOf(horaDeSaida.getTime() - horaDeEntrada.getTime()
                        )
                )
        );
        acesso.setValorAPagar(valorAPagar);

        return acesso;
    }

    public static AcessoDTO toDTO(Acesso acesso) {
        return new AcessoDTO(
                acesso.getPlacaDoCarro(),
                acesso.getHoraDeEntrada(),
                acesso.getHoraDeSaida(),
                acesso.getValorAPagar()
        );
    }
}
