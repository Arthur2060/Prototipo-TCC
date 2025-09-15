package com.senai.TCC.application.dto.create_requests.usuario;

import com.senai.TCC.model.entities.usuarios.Cliente;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record ClienteCreateResquest(
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
                examples = "2000-09-12"
        )
        Date dataNascimento
) {
    public Cliente toEntity() {
        Cliente cliente = new Cliente();

        cliente.setNome(nome);
        cliente.setEmail(email);
        cliente.setDataNascimento(dataNascimento);
        cliente.setSenha(senha);

        return cliente;
    }
}
