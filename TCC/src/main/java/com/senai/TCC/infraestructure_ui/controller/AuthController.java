package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.application.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login/dono")
    public ResponseEntity<?> loginDono(@RequestBody UsuarioLoginRequest dto) {
        return ResponseEntity.status(200).body(jwtService.loginDono(dto));
    }

    @PostMapping("/login/cliente")
    public ResponseEntity<?> loginCliente(@RequestBody UsuarioLoginRequest dto) {
        return ResponseEntity.status(200).body(jwtService.loginCliente(dto));
    }

    @PostMapping("/login/gerente")
    public ResponseEntity<?> loginGerente(@RequestBody UsuarioLoginRequest dto) {
        return ResponseEntity.status(200).body(jwtService.loginGerente(dto));
    }
}
