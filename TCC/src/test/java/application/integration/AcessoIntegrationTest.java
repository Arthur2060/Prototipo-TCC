package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.response.AcessoResponse;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.infraestructure.repositories.AcessoRepository;
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

import java.io.File;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class AcessoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AcessoRepository acessoRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;
    private Long estacionamentoId;

    @BeforeEach
    void setup() throws Exception {
        acessoRepository.deleteAll();
        estacionamentoRepository.deleteAll();
        donoRepository.deleteAll();

        // Criação do Dono
        Date birthDate = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();

        donoRepository.save(dono);

        // Gerar token JWT
        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());

        // Criar estacionamento via endpoint (para simular comportamento real)
        var estacionamentoRequest = new EstacionamentoRequest(
                "Estacionamento Central",
                "Rua A, 123",
                "11111111",
                "283",
                "foto.jpg",
                "123456",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                150,
                200,
                "123456"
        );

        var response = mockMvc.perform(withAuth(post("/estacionamento/" + dono.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(response, EstacionamentoResponse.class);
        estacionamentoId = estacionamentoResponse.id();
    }

    private AcessoRequest acessoRequestValido() {
        return new AcessoRequest(
                null,
                "ABC1234",
                Time.valueOf("08:00:00"),
                Time.valueOf("10:00:00"),
                20.0,
                estacionamentoId
        );
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    @Test
    void deveCadastrarAcessoValido() throws Exception {
        mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placaDoCarro").value("ABC1234"))
                .andExpect(jsonPath("$.valorAPagar").value(20.0));
    }

    @Test
    void deveAtualizarAcesso() throws Exception {
        var response = mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        var atualizado = new AcessoRequest(
                null,
                "XYZ9876",
                Time.valueOf("09:00:00"),
                Time.valueOf("11:00:00"),
                25.0,
                estacionamentoId
        );

        mockMvc.perform(withAuth(put("/acesso/" + acesso.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placaDoCarro").value("XYZ9876"))
                .andExpect(jsonPath("$.valorAPagar").value(25.0));
    }

    @Test
    void deveDeletarAcesso() throws Exception {
        var response = mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        mockMvc.perform(withAuth(delete("/acesso/" + acesso.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarAcessoPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        mockMvc.perform(withAuth(get("/acesso/" + acesso.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placaDoCarro").value("ABC1234"))
                .andExpect(jsonPath("$.valorAPagar").value(20.0));
    }

    @Test
    void deveListarAcessos() throws Exception {
        mockMvc.perform(withAuth(post("/acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/acesso")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placaDoCarro").value("ABC1234"));
    }

    @Test
    void deveRetornarErroAoBuscarAcessoInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/acesso/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarAcessoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/acesso/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Acesso buscado não cadastrado no sistema"));
    }
}
