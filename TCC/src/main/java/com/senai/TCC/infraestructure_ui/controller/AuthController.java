package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginResponse;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.application.mappers.usuario.ClienteMapper;
import com.senai.TCC.application.mappers.usuario.DonoMapper;
import com.senai.TCC.application.mappers.usuario.GerenteMapper;
import com.senai.TCC.application.services.AuthService;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.GerenteRepository;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.model.enums.TipoDeUsuario;
import com.senai.TCC.model.exceptions.TipoDeUsuarioInvalido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final ClienteRepository clienteRepository;
    private final GerenteRepository gerenteRepository;
    private final DonoRepository donoRepository;

    @PostMapping("/login")
    @Operation(
            method = "POST",
            summary = "Realizar login",
            description = "Retorna um token JWT caso o login seja válido",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais de acesso do usuário",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UsuarioLoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemplo de Login",
                                    value = """
                                        {
                                          "email": "admin@teste.com",
                                          "senha": "123456"
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

    @PostMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();

        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.CLIENTE.name()))) {
            return ResponseEntity.ok().body(ClienteMapper.fromEntity(clienteRepository.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.DONO.name()))) {
            return ResponseEntity.ok().body(DonoMapper.fromEntity(donoRepository.findByEmail(username).get()));
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_" + TipoDeUsuario.GERENTE.name()))){
            return ResponseEntity.ok().body(GerenteMapper.fromEntity(gerenteRepository.findByEmail(username).get()));
        } else {
            throw new TipoDeUsuarioInvalido("Tipo de usuario não encontrado no sistema!");
        }
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
