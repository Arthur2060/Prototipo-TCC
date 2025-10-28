package application.unit.security;

import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.application.services.AuthService;
import com.senai.TCC.infraestructure.repositories.usuario.UsuarioRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.entities.usuarios.Usuario;
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.model.exceptions.CredenciaisInvalidasException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarios;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtService jwt;

    @InjectMocks
    private AuthService service;

    @Test
    void deveGerarTokenQuandoCredenciaisValidas() {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "123");
        Usuario user = DonoEstacionamento.builder()
                .email("rafael@senai.com")
                .senha("encoded")
                .role(Role.ADMIN)
                .build();

        when(usuarios.findByEmail("rafael@senai.com")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "encoded")).thenReturn(true);
        when(jwt.generateToken("rafael@senai.com", "ADMIN")).thenReturn("token123");

        String token = service.login(req);

        assertEquals("token123", token);
    }

    @Test
    void deveLancarExcecaoQuandoSenhaIncorreta() {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "123");
        Usuario user = DonoEstacionamento.builder()
                .email("rafael@senai.com")
                .senha("encoded")
                .role(Role.ADMIN)
                .build();

        when(usuarios.findByEmail("rafael@senai.com")).thenReturn(Optional.of(user));
        when(encoder.matches("123", "encoded")).thenReturn(false);

        assertThrows(CredenciaisInvalidasException.class, () -> service.login(req));
    }
}
