package application.integration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.login.UsuarioLoginRequest;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.infraestructure.repositories.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private UsuarioRepository usuarios;
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private PasswordEncoder encoder;

    @BeforeEach
    void setup() {
        estacionamentoRepository.deleteAll();
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
    void deveFazerLoginERetornarToken() throws Exception {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void deveNegarLoginComSenhaInvalida() throws Exception {
        UsuarioLoginRequest req = new UsuarioLoginRequest("rafael@senai.com", "errada");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}