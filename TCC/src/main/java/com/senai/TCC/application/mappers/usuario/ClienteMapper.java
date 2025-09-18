package com.senai.TCC.application.mappers.usuario;

import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.model.entities.usuarios.Cliente;

public class ClienteMapper {

    public static Cliente toEntity(ClienteRequest dto) {
        Cliente cliente = new Cliente();

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setDataNascimento(dto.dataNascimento());
        cliente.setSenha(dto.senha());

        return cliente;
    }

    public static ClienteResponse fromEntity(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getSenha(),
                cliente.getDataNascimento(),
                cliente.getStatus()
        );
    }
}
