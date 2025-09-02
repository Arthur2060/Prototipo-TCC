package com.senai.TCC.application.dtos.usuarioDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.entities.usuarios.Usuario;
import com.senai.TCC.model.enums.TipoDeUsuario;
import com.senai.TCC.model.exceptions.TipoDeUsuarioInvalido;

import java.util.Date;

public record UsuarioDTO(
        Long id,
        @Schema(
                name = "nome",
                description = "Nome do usuario.",
                examples = "Pedro"
        )
        String nome,

        @Schema(
                name = "email",
                description = "E-mail do usuario",
                examples = "pedro.nascimento@gmail.com"
        )
        String email,

        @Schema(
                name = "senha",
                description = "Senha de acesso do usuario, " +
                        "deve conter pelo menos uma letra maiuscula, simbolo e numero",
                examples = "Estacio_2025"
        )
        String senha,

        @Schema(
                name = "dataNascimento",
                description = "Data de nascimento do usuario, deve ser maior de 18 anos",
                examples = "18-09-2000"
        )
        Date dataNascimento,

        @Schema(
                name = "tipoDeUsuario",
                description = "Um dos quatro tipos de usuario possiveis:" +
                        "CLIENTE, GERENTE ou DONO.",
                examples = "CLENTE"
        )
        TipoDeUsuario tipoDeUsuario
) {
    public Usuario toEntity() {
        Usuario usuario = switch (tipoDeUsuario) {
            case GERENTE -> new Gerente();
            case DONO -> new DonoEstacionamento();
            case CLIENTE -> new Cliente();
        };

        usuario.setNome(nome);
        usuario.setSenha(senha);
        usuario.setEmail(email);
        usuario.setDataNascimento(dataNascimento);

        return usuario;
    }

    public static UsuarioDTO fromEntity(Usuario user) {
        TipoDeUsuario tipo = switch (user) {
            case Gerente gerente -> TipoDeUsuario.GERENTE;
            case DonoEstacionamento dono -> TipoDeUsuario.DONO;
            case Cliente cliente -> TipoDeUsuario.CLIENTE;
            default -> throw new TipoDeUsuarioInvalido("O tipo de usuario da classe é invalido!");
        };

        return new UsuarioDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getSenha(),
                user.getDataNascimento(),
                tipo
        );
    }
}
