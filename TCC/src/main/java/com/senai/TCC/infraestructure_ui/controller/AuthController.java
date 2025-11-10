package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginResponse;
import com.senai.TCC.application.services.AuthService;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.Usuario;
import com.senai.TCC.model.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;

    @PostMapping("/login")
    @Operation(
            method = "POST",
            summary = "Realizar login em conta pre-existente",
            description = "Retorna um token de acesso caso o login sejá valido",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do usuario a ser logado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AcessoRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                       "email": "email@exemplo"
                                       "senha": "Senha1234"
                                     }
                                    """
                            )
                    )
            )
    )
    public ResponseEntity<UsuarioLoginResponse> login (@RequestBody UsuarioLoginRequest dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(new UsuarioLoginResponse(token));
    }

    @PostMapping("/criar-admin")
    public String criarAdminTeste() {
        Cliente u = new Cliente();
        u.setNome("ADMIN");
        u.setDataNascimento(new Date());
        u.setEmail("admin@teste.com");
        u.setSenha(passwordEncoder.encode("123456"));
        u.setRole(Role.ADMIN);
        clienteRepository.save(u);
        return "Admin criado!";
    }
}
