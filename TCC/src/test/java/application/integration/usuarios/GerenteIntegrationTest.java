package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.application.services.EstacionamentoService;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
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
    private DonoRepository donoRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;
    private Long estacionamentoId;
    private GerenteRequest testGerenteRequest;

    @BeforeEach
    void setup() {
        estacionamentoRepository.deleteAll();
        donoRepository.deleteAll();

        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Dono Teste")
                .email("dono" + System.currentTimeMillis() + "@gmail.com")
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(new Date())
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();

        dono = donoRepository.save(dono);

        var estacionamentoDto = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                null,
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

        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());
    }

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
    void deveAtualizarGerente() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(response, GerenteResponse.class);

        var updateDto = new GerenteRequest(
                "Carlos",
                "carlos@gmail.com",
                "senha123",
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
    void deveDeletarGerente() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(response, GerenteResponse.class);

        mockMvc.perform(withAuth(delete("/gerente/" + gerente.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarGerentePorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testGerenteRequest))))
                .andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(response, GerenteResponse.class);

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
