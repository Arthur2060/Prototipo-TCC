package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.AcessoResponse;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.application.services.EstacionamentoService;
import com.senai.TCC.application.services.usuario.DonoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.sql.Time;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.senai.TCC.TccApplication.class)
@AutoConfigureMockMvc
public class AcessoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EstacionamentoService estacionamentoService;

    @Autowired
    private DonoService donoService;

    private Long estacionamentoId;

    @BeforeEach
    void setUp() {
        // Cria dono válido no banco
        var donoDto = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoResponse dono = donoService.cadastrarDono(donoDto);

        // Cria estacionamento atrelado ao dono recém-criado
        var estacionamentoDto = new EstacionamentoRequest(
                "Estacionamento Central",
                "Rua A, 123",
                "11111111",
                "283",
                new File("foto.jpg"),
                "123456",
                LocalTime.of(8, 0),
                LocalTime.of(22, 0),
                150,
                200,
                "123456"
        );

        EstacionamentoResponse estacionamento = estacionamentoService.cadastrarEstacionamento(estacionamentoDto, dono.id());
        estacionamentoId = estacionamento.id();
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

    @Test
    void deveCadastrarAcessoValido() throws Exception {
        var dto = acessoRequestValido();

        mockMvc.perform(
                        post("/Acesso")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(dto))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.placaDoCarro").value("ABC1234"))
                .andExpect(jsonPath("$.valorAPagar").value(20.0));
    }

    @Test
    void deveAtualizarAcesso() throws Exception {
        var response = mockMvc.perform(
                post("/Acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))
        ).andExpect(status().isCreated()).andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        var atualizado = new AcessoRequest(
                null,
                "XYZ9876",
                Time.valueOf("09:00:00"),
                Time.valueOf("11:00:00"),
                25.0,
                estacionamentoId
        );

        mockMvc.perform(
                        put("/Acesso/" + acesso.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(atualizado))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.placaDoCarro").value("XYZ9876"))
                .andExpect(jsonPath("$.valorAPagar").value(25.0));
    }

    @Test
    void deveDeletarAcesso() throws Exception {
        var response = mockMvc.perform(
                post("/Acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))
        ).andExpect(status().isCreated()).andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        mockMvc.perform(delete("/Acesso/" + acesso.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarAcessoPorId() throws Exception {
        var response = mockMvc.perform(
                post("/Acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))
        ).andExpect(status().isCreated()).andReturn();

        AcessoResponse acesso = objectMapper.readValue(response.getResponse().getContentAsString(), AcessoResponse.class);

        mockMvc.perform(get("/Acesso/" + acesso.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placaDoCarro").value("ABC1234"))
                .andExpect(jsonPath("$.valorAPagar").value(20.0));
    }

    @Test
    void deveListarAcessos() throws Exception {
        mockMvc.perform(
                post("/Acesso")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(acessoRequestValido()))
        ).andExpect(status().isCreated());

        mockMvc.perform(get("/Acesso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placaDoCarro").value("ABC1234"));
    }

    @Test
    void deveRetornarErroAoBuscarAcessoInexistente() throws Exception {
        mockMvc.perform(get("/Acesso/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarAcessoInexistente() throws Exception {
        mockMvc.perform(delete("/Acesso/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Acesso buscado não cadastrado no sistema"));
    }
}
