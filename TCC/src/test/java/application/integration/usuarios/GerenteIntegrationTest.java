package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.io.File;
import java.time.LocalTime;
import java.sql.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = com.senai.TCC.TccApplication.class)
@AutoConfigureMockMvc
public class GerenteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long estacionamentoId;

    @BeforeEach
    void criarEstacionamento() throws Exception {
        var estacionamentoDto = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                new File("foto.jpg"),
                "123456789",
                LocalTime.of(22,0),
                LocalTime.of(8,0),
                10,
                100,
                "987654321"
        );

        var response = mockMvc.perform(
                post("/estacionamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoDto))
                        .param("donoId", "1") // ajuste conforme necessário
        ).andReturn().getResponse().getContentAsString();

        EstacionamentoResponse estacionamento = objectMapper.readValue(response, EstacionamentoResponse.class);
        estacionamentoId = estacionamento.id();
    }

    private GerenteRequest gerenteRequestValido() {
        return new GerenteRequest(
                "João",
                "joao@gmail.com",
                "senha123",
                Date.valueOf("1995-05-20"),
                "11111111111",
                estacionamentoId
        );
    }

    @Test
    void deveCadastrarGerenteValido() throws Exception {
        var dto = gerenteRequestValido();

        mockMvc.perform(
                        post("/gerente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(dto))
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("11111111111"))
                .andExpect(jsonPath("$.estacionamentoId").value(estacionamentoId));
    }

    @Test
    void deveRetornarErroAoCadastrarGerenteSemEstacionamento() throws Exception {
        var dto = new GerenteRequest(
                "João",
                "joao@gmail.com",
                "senha123",
                Date.valueOf("1995-05-20"),
                "11111111111",
                null
        );

        mockMvc.perform(
                post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroAoCadastrarGerenteComEstacionamentoInexistente() throws Exception {
        var dto = new GerenteRequest(
                "João",
                "joao@gmail.com",
                "senha123",
                Date.valueOf("1995-05-20"),
                "11111111111",
                9999L
        );

        mockMvc.perform(
                        post("/gerente")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(dto))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Id do estacionamento não encontrado no sistema"));
    }

    @Test
    void deveAtualizarGerente() throws Exception {
        var dto = gerenteRequestValido();

        var salvo = mockMvc.perform(
                post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var gerenteSalvo = objectMapper.readValue(salvo, GerenteResponse.class);

        var atualizado = new GerenteRequest(
                "Carlos",
                "carlos@gmail.com",
                "senha123",
                Date.valueOf("1995-05-20"),
                "22222222222",
                estacionamentoId
        );

        mockMvc.perform(
                        put("/gerente/" + gerenteSalvo.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(atualizado))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("22222222222"));
    }

    @Test
    void deveRetornarErroAoAtualizarGerenteComEstacionamentoInexistente() throws Exception {
        var dto = gerenteRequestValido();

        var salvo = mockMvc.perform(
                post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var gerenteSalvo = objectMapper.readValue(salvo, GerenteResponse.class);

        var atualizado = new GerenteRequest(
                "Carlos",
                "carlos@gmail.com",
                "senha123",
                Date.valueOf("1995-05-20"),
                "22222222222",
                9999L
        );

        mockMvc.perform(
                        put("/gerente/" + gerenteSalvo.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(atualizado))
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Estacionamento desejado a adicionar não cadastrado no sistema!"));
    }

    @Test
    void deveDeletarGerente() throws Exception {
        var dto = gerenteRequestValido();

        var salvo = mockMvc.perform(
                post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(salvo, GerenteResponse.class);

        mockMvc.perform(delete("/gerente/" + gerente.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarGerentePorId() throws Exception {
        var dto = gerenteRequestValido();

        var salvo = mockMvc.perform(
                post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
        ).andReturn().getResponse().getContentAsString();

        var gerente = objectMapper.readValue(salvo, GerenteResponse.class);

        mockMvc.perform(get("/gerente/" + gerente.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("11111111111"))
                .andExpect(jsonPath("$.estacionamentoId").value(estacionamentoId));
    }

    @Test
    void deveRetornarErroAoBuscarGerenteInexistente() throws Exception {
        mockMvc.perform(get("/gerente/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarGerenteInexistente() throws Exception {
        mockMvc.perform(delete("/gerente/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Id do gerente buscado não encontrado no sistema!"));
    }
}
