package com.senai.TCC.application.mappers;

import com.senai.TCC.application.dto.requests.create_requests.ReservaCreateRequest;
import com.senai.TCC.application.dto.requests.update_requests.ReservaUpdateRequest;
import com.senai.TCC.application.dto.response.ReservaResponse;
import com.senai.TCC.model.entities.Reserva;

public class ReservaMapper {

    public static Reserva toEntity(ReservaCreateRequest dto) {
        Reserva reserva = new Reserva();

        reserva.setDataDaReserva(dto.dataDaReserva());
        reserva.setHoraDaReserva(dto.horaDaReserva());
        reserva.setStatusReserva(dto.statusReserva());

        return reserva;
    }

    public static Reserva toEntity(ReservaUpdateRequest dto) {
        Reserva reserva = new Reserva();

        reserva.setStatusReserva(dto.statusReserva());
        reserva.setHoraDaReserva(dto.horaDaReserva());
        reserva.setDataDaReserva(dto.dataDaReserva());

        return reserva;
    }

    public static ReservaResponse fromEntity(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getCliente().getId(),
                reserva.getEstacionamento().getId(),
                reserva.getDataDaReserva(),
                reserva.getHoraDaReserva(),
                reserva.getStatusReserva(),
                reserva.getStatus()
        );
    }
}
