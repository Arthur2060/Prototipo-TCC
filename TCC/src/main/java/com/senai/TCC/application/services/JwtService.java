package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.requests.login.DonoLoginRequest;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class JwtService {
    private final DonoRepository donoRepository;
    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Substitua por uma chave segura
    private final Long EXPIRATION_TIME = 60 * 60 * 24000L;

    public JwtService(DonoRepository donoRepository) {
        this.donoRepository = donoRepository;
    }

    public Map<String, ?> login(DonoLoginRequest dto) {
        Optional<DonoEstacionamento> optDono = donoRepository.findByEmail(dto.email());

        if (optDono.isPresent()) {
            DonoEstacionamento dono = optDono.get();
            if (dono.getSenha().equals(dto.senha())) {
                return Map.of("token", generateToken(dono.getEmail()));
            } else {
                throw new RuntimeException("Senha incorreta");
            }
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
