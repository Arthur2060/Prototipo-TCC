package application.unit.security;

import com.senai.TCC.infraestructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {
    private JwtService jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
    }

    @Test
    void deveGerarTokenComEmailEValidarCorretamente() {
        String email = "rafael@senai.com";
        String role = "ADMIN";
        String token = jwtService.generateToken(email, role);

        assertNotNull(token);
        assertEquals(email, jwtService.extractEmail(token));
        assertEquals(role, jwtService.extractRole(token));

        UserDetails user = User.withUsername(email).password("x").roles(role).build();
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void deveDetectarTokenInvalidoComOutroUsuario() {
        String token = jwtService.generateToken("rafael@senai.com", "ADMIN");
        UserDetails user = User.withUsername("outro@senai.com").password("x").roles("ADMIN").build();

        assertFalse(jwtService.isTokenValid(token, user));
    }
}
