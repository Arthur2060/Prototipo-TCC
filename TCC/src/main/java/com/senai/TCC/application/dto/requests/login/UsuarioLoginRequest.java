package com.senai.TCC.application.dto.requests.login;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioLoginRequest(
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
        String senha
) {
}
