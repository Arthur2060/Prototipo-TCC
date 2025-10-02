package com.senai.TCC.infraestructure.config;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.infraestructure.repositories.usuario.UsuarioRepository;
import com.senai.TCC.model.entities.usuarios.Usuario;
import com.senai.TCC.model.enums.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final UsuarioRepository usuarioRepository;

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Substitua por uma chave segura
    private final Long EXPIRATION_TIME = 60 * 60 * 24000L;

    public JwtService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String generateToken(String email, String roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(System.currentTimeMillis() + EXPIRATION_TIME)))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
