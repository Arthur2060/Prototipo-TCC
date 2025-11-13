package com.senai.TCC.application.mappers;

import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.dto.response.AcessoResponse;
import com.senai.TCC.infraestructure.repositories.CarroRepository;
import com.senai.TCC.model.entities.Acesso;

public class AcessoMapper {
    public static Acesso toEntity(AcessoRequest dto) {
        Acesso acesso = new Acesso();

        acesso.setPlacaDoCarro(dto.placaDoCarro());
        acesso.setHoraDeEntrada(dto.horaDeEntrada());
        acesso.setValorAPagar(dto.valorAPagar());
        acesso.setHoraDeSaida(dto.horaDeSaida());
        acesso.calcularHorasTotais();

        return acesso;
    }

    public static AcessoResponse fromEntity(Acesso acesso) {
        return new AcessoResponse(
                acesso.getId(),
                acesso.getCarro().getId(),
                acesso.getPlacaDoCarro(),
                acesso.getHoraDeEntrada(),
                acesso.getHoraDeSaida(),
                acesso.getTotalHoras(),
                acesso.getValorAPagar(),
                acesso.getEstacionamento().getId(),
                acesso.getStatus()
        );
    }
}
