package com.senai.TCC.application.mappers.usuario;

import com.senai.TCC.application.dto.requests.create_requests.usuario.UsuarioCreateRequest;
import com.senai.TCC.application.dto.response.usuario.UsuarioResponse;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.entities.usuarios.Usuario;
import com.senai.TCC.model.enums.TipoDeUsuario;
import com.senai.TCC.model.exceptions.TipoDeUsuarioInvalido;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioCreateRequest dto) {
        Usuario usuario = switch (dto.tipoDeUsuario()) {
            case GERENTE -> new Gerente();
            case DONO -> new DonoEstacionamento();
            case CLIENTE -> new Cliente();
        };

        usuario.setNome(dto.nome());
        usuario.setSenha(dto.senha());
        usuario.setEmail(dto.email());
        usuario.setDataNascimento(dto.dataNascimento());

        return usuario;
    }


    public static UsuarioResponse fromEntity(Usuario user) {
        TipoDeUsuario tipo = switch (user) {
            case Gerente gerente -> TipoDeUsuario.GERENTE;
            case DonoEstacionamento dono -> TipoDeUsuario.DONO;
            case Cliente cliente -> TipoDeUsuario.CLIENTE;
            default -> throw new TipoDeUsuarioInvalido("O tipo de usuario da classe é invalido!");
        };

        return new UsuarioResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getSenha(),
                user.getDataNascimento(),
                tipo,
                user.getStatus()
        );
    }
}
