package application.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.infraestructure.repositories.usuario.UsuarioRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.infraestructure.security.UsuarioDetailService;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
class JwtAuthenticationFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UsuarioRepository usuarios;
    @Autowired
    private PasswordEncoder encoder;

    @Mock
    private UsuarioDetailService usuarioDetailService;

    @BeforeEach
    void setup() {
        usuarios.deleteAll();
        LocalDate localDate = LocalDate.of(1990, 1, 1);
        Date dataNascimento = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        usuarios.save(DonoEstacionamento.builder()
                .nome("Rafael")
                .dataNascimento(dataNascimento)
                .email("rafael@senai.com")
                .senha(encoder.encode("123456"))
                .role(Role.ADMIN)
                .build());
    }

    @Test
    void deveRetornar405QuandoMetodoHttpNaoSuportado() throws Exception {
        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void devePermitirAcessoAoLoginSemTokenMesmoComSenhaInvalida() throws Exception {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "senhaErrada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveNegarAcessoAEndpointProtegidoSemToken() throws Exception {
        mockMvc.perform(get("/estacionamento"))
                .andExpect(status().isForbidden());
    }

    @Test
    void devePermitirAcessoComTokenValido() throws Exception {
        String email = "rafael@senai.com";
        String token = jwtService.generateToken(email, "ADMIN");

        UserDetails mockUser = User.withUsername(email)
                .password("encoded")
                .roles("ADMIN")
                .build();

        when(usuarioDetailService.loadUserByUsername(email)).thenReturn(mockUser);

        mockMvc.perform(get("/estacionamento")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}