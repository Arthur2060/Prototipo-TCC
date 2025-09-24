package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.senai.TCC.TccApplication.class)
@AutoConfigureMockMvc
public class EstacionamentoIntegrationTest {
    /*TODO*/
    //Refazer tudo
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long donoId;

    @BeforeEach
    void setUp() throws Exception {
        // Cria um Dono antes de cada teste
        var donoRequest = new DonoRequest(
                "Dono Teste",
                "dono@teste.com",
                "senha123",
                java.sql.Date.valueOf("1990-01-01")
        );

        var donoJson = mockMvc.perform(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donoRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DonoResponse donoResponse = objectMapper.readValue(donoJson, DonoResponse.class);
        donoId = donoResponse.id();
    }

    private EstacionamentoRequest estacionamentoRequest(String nome) {
        return new EstacionamentoRequest(
                nome,
                "Rua das Flores",
                "12345-678",
                "123",
                null, // Evita erro com arquivo inexistente
                "123456789",
                LocalTime.of(22, 0),
                LocalTime.of(8, 0),
                10,
                100,
                "987654321"
        );
    }

    @Test
    void deveCadastrarEstacionamentoValido() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");

        mockMvc.perform(post("/estacionamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("donoId", donoId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("EstacioPlay"));
    }

    @Test
    void deveAtualizarEstacionamento() throws Exception {
        // Cria estacionamento antes de atualizar
        var dto = estacionamentoRequest("EstacioPlay");
        var salvoJson = mockMvc.perform(post("/estacionamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("donoId", donoId.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoSalvo = objectMapper.readValue(salvoJson, EstacionamentoResponse.class);

        // Novo objeto para atualizar
        var atualizado = estacionamentoRequest("NovoNome");

        mockMvc.perform(put("/estacionamento/" + estacionamentoSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("NovoNome"));
    }

    @Test
    void deveDeletarEstacionamento() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");
        var salvoJson = mockMvc.perform(post("/estacionamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("donoId", donoId.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoSalvo = objectMapper.readValue(salvoJson, EstacionamentoResponse.class);

        mockMvc.perform(delete("/estacionamento/" + estacionamentoSalvo.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarEstacionamentoPorId() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");
        var salvoJson = mockMvc.perform(post("/estacionamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("donoId", donoId.toString()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoSalvo = objectMapper.readValue(salvoJson, EstacionamentoResponse.class);

        mockMvc.perform(get("/estacionamento/" + estacionamentoSalvo.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("EstacioPlay"));
    }

    @Test
    void deveListarEstacionamentos() throws Exception {
        var dto = estacionamentoRequest("EstacioPlay");
        mockMvc.perform(post("/estacionamento")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("donoId", donoId.toString()))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/estacionamento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("EstacioPlay"));
    }
}
