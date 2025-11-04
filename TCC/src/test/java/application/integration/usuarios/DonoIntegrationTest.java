package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.model.enums.TipoDeUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class DonoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;

    private DonoRequest testDonoRequest;

    @BeforeEach
    void setup() {
        donoRepository.deleteAll();

        Date birthDate = Date.from(LocalDate.of(1985, 5, 20)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "dono" + System.currentTimeMillis() + "@gmail.com";

        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Carlos")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();

        donoRepository.save(dono);

        testDonoRequest = new DonoRequest(
                "Carlos",
                "carlos@gmail.com",
                "123456",
                birthDate
        );

        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());
    }

    // Adiciona Authorization header
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    @Test
    void deveCadastrarDonoValido() throws Exception {
        mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testDonoRequest))))
                .andExpect(status().isCreated());
    }

    @Test
    void deveAtualizarDono() throws Exception {
        // Cria um novo Dono para o teste de atualização
        var novoDonoRequest = new DonoRequest(
                "Dono para Atualizar",
                "dono.update" + System.currentTimeMillis() + "@gmail.com",
                "senha123",
                new Date()
        );

        var savedResponse = mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var donoSalvo = objectMapper.readValue(savedResponse, DonoResponse.class);

        var atualizado = new DonoRequest(
                "Carlos Atualizado",
                "carlos.atualizado@gmail.com",
                "654321",
                new Date()
        );

        mockMvc.perform(withAuth(put("/dono/" + donoSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"));
    }

    @Test
    void deveDeletarDono() throws Exception {
        // Cria um novo Dono para o teste de exclusão
        var novoDonoRequest = new DonoRequest(
                "Dono para Deletar",
                "dono.delete" + System.currentTimeMillis() + "@gmail.com",
                "senha123",
                new Date()
        );

        var savedResponse = mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var donoSalvo = objectMapper.readValue(savedResponse, DonoResponse.class);

        mockMvc.perform(withAuth(delete("/dono/" + donoSalvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroAoDeletarDonoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/dono/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Dono buscado não cadastrado no sistema"));
    }
}
