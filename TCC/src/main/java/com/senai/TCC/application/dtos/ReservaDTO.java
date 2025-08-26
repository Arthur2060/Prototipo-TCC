package com.senai.TCC.application.dtos;

import com.senai.TCC.model.enums.StatusReserva;
import io.swagger.v3.oas.annotations.media.Schema;
import com.senai.TCC.model.entities.Reserva;

import java.sql.Time;
import java.util.Date;

public record ReservaDTO(
        @Schema(
                name = "ID do Usuario",
                description = "Usuario que solicitou a reserva"
        )
        Long usuarioId,
        @Schema(
                name = "ID do estacionamento",
                description = "Estabelecimento cadastrado em que a reserva foi solicitada"
        )
        Long estacioId,
        @Schema(
                name = "Data da reserva",
                description = "Data em que a reserva foi feita, necessario ser futura",
                examples = "2023-12-25"
        )
        Date dataDaReserva,
        @Schema(
                name = "Hora da reserva",
                description = "Hora em que a reserva foi feita, necessario estar dentro do horario de funcionamento do estacionamento",
                examples = "14:30"
        )
        Time horaDaReserva,
        StatusReserva status
) {
    public Reserva fromDTO() {
        Reserva reserva = new Reserva();

        reserva.setDataDaReserva(dataDaReserva);
        reserva.setHoraDaReserva(horaDaReserva);
        reserva.setStatus(status);

        return reserva;
    }

    public static ReservaDTO toDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getCliente().getId(),
                reserva.getEstacionamento().getId(),
                reserva.getDataDaReserva(),
                reserva.getHoraDaReserva(),
                reserva.getStatus()
        );
    }
}
