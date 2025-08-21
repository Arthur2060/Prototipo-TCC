package application.dtos;

import model.entities.Acesso;

import java.sql.Time;

public record AcessoDTO(
        String placaDoCarro,
        Time horaDeEntrada,
        Time horaDeSaida,
        Integer totalHoras,
        Double valorAPagar
) {
    public Acesso fromDTO() {
        Acesso acesso = new Acesso();

        acesso.setPlacaDoCarro(placaDoCarro);
        acesso.setHoraDeEntrada(horaDeEntrada);
        acesso.setHoraDeSaida(horaDeSaida);
        acesso.setTotalHoras(totalHoras);
        acesso.setValorAPagar(valorAPagar);

        return acesso;
    }

    public static AcessoDTO toDTO(Acesso acesso) {
        return new AcessoDTO(
                acesso.getPlacaDoCarro(),
                acesso.getHoraDeEntrada(),
                acesso.getHoraDeSaida(),
                acesso.getTotalHoras(),
                acesso.getValorAPagar()
        );
    }
}
