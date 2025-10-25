package com.senai.TCC.application.mappers.usuario;

import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.enums.Role;

public class GerenteMapper {

    public static Gerente toEntity(GerenteRequest dto) {
        Gerente gerente = new Gerente();

        gerente.setNome(dto.nome());
        gerente.setCpfOuCnpj(dto.cpfOuCnpj());
        gerente.setEmail(dto.email());
        gerente.setSenha(dto.senha());
        gerente.setDataNascimento(dto.dataNascimento());
        gerente.setRole(Role.GERENTE);

        return gerente;
    }


    public static GerenteResponse fromEntity(Gerente gerente) {
        return new GerenteResponse(
                gerente.getId(),
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getSenha(),
                gerente.getDataNascimento(),
                gerente.getCpfOuCnpj(),
                gerente.getEstacionamento().getId(),
                gerente.getStatus()
        );
    }
}
