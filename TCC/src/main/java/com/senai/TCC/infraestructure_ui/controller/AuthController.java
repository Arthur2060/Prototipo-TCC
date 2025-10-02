package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginResponse;
import com.senai.TCC.application.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UsuarioLoginResponse> login (@RequestBody UsuarioLoginRequest dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(new UsuarioLoginResponse(token));
    }
}
