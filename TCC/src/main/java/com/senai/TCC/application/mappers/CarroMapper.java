package com.senai.TCC.application.mappers;

import com.senai.TCC.application.dto.create_requests.CarroCreateRequest;
import com.senai.TCC.application.dto.response.CarroResponse;
import com.senai.TCC.model.entities.Carro;

public class CarroMapper {

    public static Carro toEntity(CarroCreateRequest dto) {
        Carro carro = new Carro();

        carro.setCor(dto.cor());
        carro.setModelo(dto.modelo());
        carro.setPlaca(dto.placa());

        return carro;
    }

    public static CarroResponse fromEntity(Carro carro) {
        return new CarroResponse(
                carro.getId(),
                carro.getCliente().getId(),
                carro.getPlaca(),
                carro.getModelo(),
                carro.getCor()
        );
    }
}
