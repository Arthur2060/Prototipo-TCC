package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.application.services.EstacionamentoService;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.sql.Date;
import java.time.LocalTime;

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

    private Long estacionamentoId;

    @BeforeEach
    void setup() {
        // cria dono válido no banco
        DonoEstacionamento dono = new DonoEstacionamento();
        dono.setNome("Dono Teste");
        dono.setEmail("dono@teste.com");
        dono.setSenha("123456");
        dono = donoRepository.save(dono);

        // cria estacionamento associado ao dono
        var estacionamentoDto = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                new File("foto.jpg"),
                "123456789",
                LocalTime.of(22, 0),
                LocalTime.of(8, 0),
                10,
                100,
                "987654321"
        );

        EstacionamentoResponse estacionamento =
                estacionamentoService.cadastrarEstacionamento(estacionamentoDto, dono.getId());
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

        mockMvc.perform(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("João"))
                .andExpect(jsonPath("$.cpfOuCnpj").value("11111111111"))
                .andExpect(jsonPath("$.estacionamentoId").value(estacionamentoId));
    }

    @Test
    void deveRetornarErroAoCadastrarGerenteSemEstacionamento() throws Exception {
        var dto = new GerenteRequest(
                "João", "joao@gmail.com", "senha123",
                Date.valueOf("1995-05-20"), "11111111111", null
        );

        mockMvc.perform(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarErroAoCadastrarGerenteComEstacionamentoInexistente() throws Exception {
        var dto = new GerenteRequest(
                "João", "joao@gmail.com", "senha123",
                Date.valueOf("1995-05-20"), "11111111111", 9999L
        );

        mockMvc.perform(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarGerente() throws Exception {
        var dto = gerenteRequestValido();

        var response = mockMvc.perform(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        GerenteResponse gerente = objectMapper.readValue(
                response.getResponse().getContentAsString(),
                GerenteResponse.class
        );

        var updateDto = new GerenteRequest(
                "Carlos", "carlos@gmail.com", "senha123",
                Date.valueOf("1995-05-20"), "22222222222", estacionamentoId
        );

        mockMvc.perform(put("/gerente/" + gerente.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos"));
    }

    @Test
    void deveRetornarErroAoAtualizarGerenteComEstacionamentoInexistente() throws Exception {
        var updateDto = new GerenteRequest(
                "Carlos", "carlos@gmail.com", "senha123",
                Date.valueOf("1995-05-20"), "22222222222", 9999L
        );

        mockMvc.perform(put("/gerente/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveDeletarGerente() throws Exception {
        var dto = gerenteRequestValido();

        var response = mockMvc.perform(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        GerenteResponse gerente = objectMapper.readValue(
                response.getResponse().getContentAsString(),
                GerenteResponse.class
        );

        mockMvc.perform(delete("/gerente/" + gerente.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarGerentePorId() throws Exception {
        var dto = gerenteRequestValido();

        var response = mockMvc.perform(post("/gerente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        GerenteResponse gerente = objectMapper.readValue(
                response.getResponse().getContentAsString(),
                GerenteResponse.class
        );

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
