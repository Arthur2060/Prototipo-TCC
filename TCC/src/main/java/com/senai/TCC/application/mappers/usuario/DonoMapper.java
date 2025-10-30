package com.senai.TCC.application.mappers.usuario;

import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.enums.Role;

public class DonoMapper {

    public static DonoEstacionamento toEntity(DonoRequest dto) {
        DonoEstacionamento dono = new DonoEstacionamento();

        dono.setNome(dto.nome());
        dono.setEmail(dto.email());
        dono.setSenha(dto.senha());
        dono.setDataNascimento(dto.dataNascimento());
        dono.setRole(Role.DONO);

        return dono;
    }

    public static DonoResponse fromEntity(DonoEstacionamento dono) {
        return new DonoResponse(
                dono.getId(),
                dono.getNome(),
                dono.getEmail(),
                dono.getSenha(),
                dono.getDataNascimento(),
                dono.getStatus()
        );
    }
}
