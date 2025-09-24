package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.login.DonoLoginRequest;
import com.senai.TCC.application.services.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DonoLoginRequest dto) {
        return ResponseEntity.status(200).body(jwtService.login(dto));
    }
}
