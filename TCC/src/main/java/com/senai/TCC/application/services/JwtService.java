package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.application.mappers.usuario.UsuarioMapper;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.GerenteRepository;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.entities.usuarios.Usuario;
import com.senai.TCC.model.enums.TipoDeUsuario;
import com.senai.TCC.model.exceptions.TipoDeUsuarioInvalidoException;
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
    private final GerenteRepository gerenteRepository;
    private final ClienteRepository clienteRepository;

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Substitua por uma chave segura
    private final Long EXPIRATION_TIME = 60 * 60 * 24000L;

    public JwtService(DonoRepository donoRepository, GerenteRepository gerenteRepository, ClienteRepository clienteRepository) {
        this.donoRepository = donoRepository;
        this.gerenteRepository = gerenteRepository;
        this.clienteRepository = clienteRepository;
    }

    public Map<String, ?> loginDono(UsuarioLoginRequest dto) {
        Optional<DonoEstacionamento> optUsuario = donoRepository.findByEmail(dto.email());

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            if (usuario.getSenha().equals(dto.senha())) {
                return Map.of("token", generateToken(usuario));
            } else {
                throw new RuntimeException("Senha incorreta");
            }
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public Map<String, ?> loginCliente(UsuarioLoginRequest dto) {
        Optional<Cliente> optUsuario = clienteRepository.findByEmail(dto.email());

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            if (usuario.getSenha().equals(dto.senha())) {
                return Map.of("token", generateToken(usuario));
            } else {
                throw new RuntimeException("Senha incorreta");
            }
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public Map<String, ?> loginGerente(UsuarioLoginRequest dto) {
        Optional<Gerente> optUsuario = gerenteRepository.findByEmail(dto.email());

        if (optUsuario.isPresent()) {
            Usuario usuario = optUsuario.get();
            if (usuario.getSenha().equals(dto.senha())) {
                return Map.of("token", generateToken(usuario));
            } else {
                throw new RuntimeException("Senha incorreta");
            }
        } else {
            throw new RuntimeException("Usuário não encontrado");
        }
    }

    public String generateToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .claim("id", usuario.getId())
                .claim("senha", usuario.getSenha())
                .claim("nome", usuario.getNome())
                .claim("tipo", usuario.getClass())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
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
