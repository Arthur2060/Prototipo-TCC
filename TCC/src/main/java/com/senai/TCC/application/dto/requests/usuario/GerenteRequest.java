package com.senai.TCC.application.dto.requests.usuario;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

public record GerenteRequest(
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
        Date dataNascimento,

        @Schema(
                name = "cpfOuCnpj",
                description = "Documento oficial necessário para contratar um gerente",
                examples = "123.456.789-10"
        )
        String cpfOuCnpj,

        @Schema(
                name = "estacionamentoId",
                description = "ID do estacionamento que o gerente irá gerenciar",
                examples = "1"
        )
        Long estacionamentoId
) {
}
