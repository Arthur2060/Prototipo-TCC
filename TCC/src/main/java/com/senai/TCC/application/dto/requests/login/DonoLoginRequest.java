package com.senai.TCC.application.dto.requests.login;

public record DonoLoginRequest(
        String email,
        String senha
) {
}
