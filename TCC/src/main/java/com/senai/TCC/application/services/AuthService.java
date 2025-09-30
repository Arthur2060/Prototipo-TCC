package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.infraestructure.config.JwtService;
import com.senai.TCC.infraestructure.repositories.usuario.UsuarioRepository;
import com.senai.TCC.model.entities.usuarios.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public String login(UsuarioLoginRequest req) {
        Usuario usuario = usuarios.findByEmail(req.email())
                .orElseThrow(() ->  new RuntimeException("Usuário não encontrado"));

        if (!encoder.matches(req.senha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas");
        }

        return jwt.generateToken(usuario.getEmail(), usuario.getRole().name());
    }
}
