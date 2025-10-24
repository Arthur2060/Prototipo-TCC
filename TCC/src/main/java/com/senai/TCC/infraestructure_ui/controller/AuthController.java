package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginResponse;
import com.senai.TCC.application.services.AuthService;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.Usuario;
import com.senai.TCC.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;

    @PostMapping("/login")
    public ResponseEntity<UsuarioLoginResponse> login (@RequestBody UsuarioLoginRequest dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(new UsuarioLoginResponse(token));
    }

    @PostMapping("/criar-admin")
    public String criarAdminTeste() {
        Cliente u = new Cliente();
        u.setEmail("admin@teste.com");
        u.setSenha(passwordEncoder.encode("123456"));
        u.setRole(Role.ADMIN);
        clienteRepository.save(u);
        return "Admin criado!";
    }
}
