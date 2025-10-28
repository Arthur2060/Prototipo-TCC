package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.application.services.EstacionamentoService;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class GerenteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private Long estacionamentoId;
    private String token;
    private GerenteRequest testGerenteRequest;

    @BeforeEach
    void setup() {
        donoRepository.deleteAll();

        // cria dono válido
        DonoEstacionamento dono = new DonoEstacionamento();
        dono.setNome("Dono Teste");
        dono.setEmail("dono@teste.com");
        dono.setSenha(passwordEncoder.encode("123456"));
        dono = donoRepository.save(dono);

        // cria estacionamento associado ao dono
        var estacionamentoDto = new com.senai.TCC.application.dto.requests.EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                null, // omit file upload for test simplicity
                "123456789",
                java.time.LocalTime.of(22, 0),
                java.time.LocalTime.of(8, 0),
                10,
                100,
                "987654321"
        );

        estacionamentoId = estacionamentoService
                .cadastrarEstacionamento(estacionamentoDto, dono.getId())
                .id();

        // reusable GerenteRequest
        Date birthDate = Date.from(LocalDate.of(1995, 5, 20)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        testGerenteRequest = new GerenteRequest(
                "João",
                "joao@gmail.com",
                "senha123",
                birthDate,
                "11111111111",
                estacionamentoId
        );

        token = jwtService.generateToken(dono.getEmail(), "ADMIN");
    }

    // Helper method to add Authorization header
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    @Test
    void deveCadastrarGerenteValido() throws Exception {
        mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("11111111111"))
                .andExpect(jsonPath("$.estacionamentoId").value(estacionamentoId));
    }

    @Test
    void deveRetornarErroAoCadastrarGerenteSemEstacionamento() throws Exception {
        var dto = new GerenteRequest(
                "João", "joao@gmail.com", "senha123",
                testGerenteRequest.dataNascimento(),
                "11111111111",
                null
        );

        mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroAoCadastrarGerenteComEstacionamentoInexistente() throws Exception {
        var dto = new GerenteRequest(
                "João", "joao@gmail.com", "senha123",
                testGerenteRequest.dataNascimento(),
                "11111111111",
                9999L
        );

        mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarGerente() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andExpect(status().isCreated())
                .andReturn();

        GerenteResponse gerente = objectMapper.readValue(
                response.getResponse().getContentAsString(),
                GerenteResponse.class
        );

        var updateDto = new GerenteRequest(
                "Carlos", "carlos@gmail.com", "senha123",
                testGerenteRequest.dataNascimento(),
                "22222222222",
                estacionamentoId
        );

        mockMvc.perform(withAuth(put("/gerente/" + gerente.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateDto))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"));
    }

    @Test
    void deveRetornarErroAoAtualizarGerenteComEstacionamentoInexistente() throws Exception {
        var updateDto = new GerenteRequest(
                "Carlos", "carlos@gmail.com", "senha123",
                testGerenteRequest.dataNascimento(),
                "22222222222",
                9999L
        );

        mockMvc.perform(withAuth(put("/gerente/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateDto))))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarGerente() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andExpect(status().isCreated())
                .andReturn();

        GerenteResponse gerente = objectMapper.readValue(
                response.getResponse().getContentAsString(),
                GerenteResponse.class
        );

        mockMvc.perform(withAuth(delete("/gerente/" + gerente.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarGerentePorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andExpect(status().isCreated())
                .andReturn();

        GerenteResponse gerente = objectMapper.readValue(
                response.getResponse().getContentAsString(),
                GerenteResponse.class
        );

        mockMvc.perform(withAuth(get("/gerente/" + gerente.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("11111111111"))
                .andExpect(jsonPath("$.estacionamentoId").value(estacionamentoId));
    }

    @Test
    void deveRetornarErroAoBuscarGerenteInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/gerente/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarGerenteInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/gerente/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Id do gerente buscado não encontrado no sistema!"));
    }
}
