package com.senai.TCC.application.dto.create_requests.usuario;

import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record DonoCreateRequest(
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
    public DonoEstacionamento toEntity() {
        DonoEstacionamento dono = new DonoEstacionamento();

        dono.setNome(nome);
        dono.setEmail(email);
        dono.setSenha(senha);
        dono.setDataNascimento(dataNascimento);

        return dono;
    }
}
