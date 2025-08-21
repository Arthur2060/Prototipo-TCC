package application.dtos;

import model.entities.Reserva;

import java.sql.Time;
import java.util.Date;

public record ReservaDTO(
        Date dataDaReserva,
        Time horaDaReserva
) {
    public Reserva fromDTO() {
        Reserva reserva = new Reserva();

        reserva.setDataDaReserva(dataDaReserva);
        reserva.setHoraDaReserva(horaDaReserva);

        return reserva;
    }

    public static ReservaDTO toDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getDataDaReserva(),
                reserva.getHoraDaReserva()
        );
    }
}
