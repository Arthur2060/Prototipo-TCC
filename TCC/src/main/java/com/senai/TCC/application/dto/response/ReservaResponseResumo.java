package com.senai.TCC.application.dto.response;

import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.model.enums.StatusReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.sql.Time;
import java.util.Date;

public record ReservaResponseResumo(
        Long id,
        @Schema(
                name = "cliente",
                description = "Cliente que solicitou a reserva"
        )
        ClienteResponse cliente,
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
        @Schema(
                name = "statusReserva",
                description = "Status atual da reserva",
                examples = "PENDENTE"
        )
        StatusReserva statusReserva,
        @Schema(
                name = "status",
                description = "Status da entidade, se está ativa ou inativa no sistema",
                examples = "true"
        )
        Boolean status
) {
}
