package com.senai.TCC.application.dto.requests.update_requests;

import com.senai.TCC.model.enums.StatusReserva;

import java.sql.Time;
import java.util.Date;

public record ReservaUpdateRequest(
        StatusReserva statusReserva,
        Date dataDaReserva,
        Time horaDaReserva
) {
}
