package com.senai.TCC.infraestructure.security;

import com.senai.TCC.infraestructure.repositories.usuario.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final Key SECRET_KEY;
    private final long EXPIRATION_TIME;
    private final long REFRESH_TIME;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.access-expiration:900}") long accessExpSeconds, // 15min
            @Value("${security.jwt.refresh-expiration:604800}") long refreshExpSeconds // 7 dias
    ) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
        this.EXPIRATION_TIME = accessExpSeconds;
        this.REFRESH_TIME = refreshExpSeconds;
    }
    public String generateToken(String email, String roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .claim("role", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(EXPIRATION_TIME)))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(REFRESH_TIME)))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return parceClaims(token).getSubject();
    }

    public Claims parceClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractRole(String token) {
        return (String) parceClaims(token).get("role");
    }
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = parceClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public boolean isValid(String token) {
        try {
            parceClaims(token);
            return true;
        } catch(JwtException e) {
            return false;
        }
    }
}
