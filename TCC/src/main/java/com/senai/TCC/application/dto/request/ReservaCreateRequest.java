package com.senai.TCC.application.dto.request;

import com.senai.TCC.model.entities.Reserva;
import com.senai.TCC.model.enums.StatusReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Time;
import java.util.Date;

public record ReservaCreateRequest(
        @Schema(
                name = "clienteId",
                description = "Cliente que solicitou a reserva"
        )
        Long clienteId,
        @Schema(
                name = "estacioId",
                description = "Estabelecimento cadastrado em que a reserva foi solicitada"
        )
        Long estacioId,
        @Schema(
                name = "dataDaReserva",
                description = "Data em que a reserva foi feita, necessario ser futura",
                examples = "2023-12-25"
        )
        Date dataDaReserva,
        @Schema(
                name = "horaDaReserva",
                description = "Hora em que a reserva foi feita, necessario estar dentro do horario de funcionamento do estacionamento",
                examples = "14:30:00",
                type = "string",
                pattern = "HH:mm:ss"
        )
        Time horaDaReserva,
        StatusReserva status
) {
    public Reserva toEntity() {
        Reserva reserva = new Reserva();

        reserva.setDataDaReserva(dataDaReserva);
        reserva.setHoraDaReserva(horaDaReserva);
        reserva.setStatus(status);

        return reserva;
    }
}
