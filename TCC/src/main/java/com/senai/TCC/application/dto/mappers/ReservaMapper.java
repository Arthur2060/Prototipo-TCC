package com.senai.TCC.application.dto.mappers;

import com.senai.TCC.application.dto.create_requests.ReservaCreateRequest;
import com.senai.TCC.application.dto.response.ReservaResponse;
import com.senai.TCC.model.entities.Reserva;

public class ReservaMapper {

    public static Reserva toEntity(ReservaCreateRequest dto) {
        Reserva reserva = new Reserva();

        reserva.setDataDaReserva(dto.dataDaReserva());
        reserva.setHoraDaReserva(dto.horaDaReserva());
        reserva.setStatus(dto.status());

        return reserva;
    }

    public static ReservaResponse fromEntity(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getCliente().getId(),
                reserva.getEstacionamento().getId(),
                reserva.getDataDaReserva(),
                reserva.getHoraDaReserva(),
                reserva.getStatus()
        );
    }
}
