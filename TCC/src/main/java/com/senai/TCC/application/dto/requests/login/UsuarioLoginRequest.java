package com.senai.TCC.application.dto.requests.login;

import io.swagger.v3.oas.annotations.media.Schema;

public record UsuarioLoginRequest(
        @Schema(
                description = "E-mail do usuário",
                example = "usuario@exemplo.com"
        )
        String email,

        @Schema(
                description = "Senha do usuário. Deve conter letra maiúscula, símbolo e número.",
                example = "Estacio_2025"
        )
        String senha
) {
}
