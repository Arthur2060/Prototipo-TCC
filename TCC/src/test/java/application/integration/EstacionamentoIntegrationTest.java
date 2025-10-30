package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class EstacionamentoIntegrationTest {

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
    private Long donoId;

    @BeforeEach
    void setup() {
        donoRepository.deleteAll();

        Date birthDate = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Dono Teste")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("senha123"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();

        donoRepository.save(dono);
        donoId = dono.getId();

        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());
    }

    // Helper para adicionar Authorization Header
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private EstacionamentoRequest estacionamentoRequest(String nome) {
        return new EstacionamentoRequest(
                nome,
                "Rua das Flores",
                "12345-678",
                "123",
                null, // sem arquivo para evitar erro
                "123456789",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                10,
                100,
                "987654321"
        );
    }

    @Test
    void deveCadastrarEstacionamentoValido() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");

        mockMvc.perform(withAuth(post("/estacionamento/" + donoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("EstacioPlay"));

    }

    @Test
    void deveAtualizarEstacionamento() throws Exception {
        // cria estacionamento
        var dto = estacionamentoRequest("EstacioPlay");
        var salvoJson = mockMvc.perform(withAuth(post("/estacionamento/" + donoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse salvo = objectMapper.readValue(salvoJson, EstacionamentoResponse.class);

        // atualiza
        var atualizado = estacionamentoRequest("NovoNome");

        mockMvc.perform(withAuth(put("/estacionamento/" + salvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("NovoNome"));
    }

    @Test
    void deveDeletarEstacionamento() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");
        var salvoJson = mockMvc.perform(withAuth(post("/estacionamento/" + donoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse salvo = objectMapper.readValue(salvoJson, EstacionamentoResponse.class);

        mockMvc.perform(withAuth(delete("/estacionamento/" + salvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroAoDeletarEstacionamentoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/estacionamento/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("O Id do estacionamento fornecido não foi encontrado no sistema!"));
    }

    @Test
    void deveBuscarEstacionamentoPorId() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");
        var salvoJson = mockMvc.perform(withAuth(post("/estacionamento/" + donoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse salvo = objectMapper.readValue(salvoJson, EstacionamentoResponse.class);

        mockMvc.perform(withAuth(get("/estacionamento/" + salvo.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("EstacioPlay"));
    }

    @Test
    void deveListarEstacionamentos() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");
        mockMvc.perform(withAuth(post("/estacionamento/" + donoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/estacionamento")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("EstacioPlay"));
    }
}
