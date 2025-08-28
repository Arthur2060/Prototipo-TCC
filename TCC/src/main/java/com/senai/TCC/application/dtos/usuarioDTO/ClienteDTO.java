package com.senai.TCC.application.dtos.usuarioDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import com.senai.TCC.model.entities.usuarios.Cliente;

import java.util.Date;

public record ClienteDTO(
        @Schema(
                name = "Nome",
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
                name = "Senha",
                description = "Senha de acesso do usuario, " +
                        "deve conter pelo menos uma letra maiuscula, simbolo e numero",
                examples = "Estacio_2025"
        )
        String senha,

        @Schema(
                name = "Data de nascimento",
                description = "Data de nascimento do usuario, deve ser maior de 18 anos",
                examples = "18-09-2000"
        )
        Date dataNascimento
) {
    public Cliente fromDTO() {
        Cliente cliente = new Cliente();

        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setDataNascimento(dataNascimento);
        cliente.setSenha(senha);

        return cliente;
    }

    public static ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getSenha(),
                cliente.getDataNascimento()
        );
    }
}
