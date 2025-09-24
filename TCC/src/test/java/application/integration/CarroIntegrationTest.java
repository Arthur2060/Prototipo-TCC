package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.CarroRequest;
import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.CarroResponse;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.senai.TCC.TccApplication.class)
@AutoConfigureMockMvc
public class CarroIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long clienteId;

    @BeforeEach
    void setUp() throws Exception {
        // Criar cliente antes de cada teste
        var clienteRequest = new ClienteRequest(
                "Cliente Teste",
                "cliente@teste.com",
                "senha456",
                java.sql.Date.valueOf("1995-05-05")
        );

        var clienteResponseJson = mockMvc.perform(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteResponse clienteResponse = objectMapper.readValue(clienteResponseJson, ClienteResponse.class);
        clienteId = clienteResponse.id();
    }

    @Test
    void deveCadastrarCarroValido() throws Exception {
        var dto = new CarroRequest(clienteId, "EUD-8679", "Corsa", "Fuxia");

        mockMvc.perform(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("EUD-8679"))
                .andExpect(jsonPath("$.modelo").value("Corsa"))
                .andExpect(jsonPath("$.cor").value("Fuxia"));
    }

    @Test
    void deveAtualizarCarro() throws Exception {
        // Criar carro primeiro
        var dto = new CarroRequest(clienteId, "EUD-8679", "Corsa", "Fuxia");
        var salvoJson = mockMvc.perform(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CarroResponse carroSalvo = objectMapper.readValue(salvoJson, CarroResponse.class);

        // Atualizar carro
        var atualizado = new CarroRequest(clienteId, "ABC-1234", "Onix", "Preto");

        mockMvc.perform(put("/carro/" + carroSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC-1234"))
                .andExpect(jsonPath("$.modelo").value("Onix"))
                .andExpect(jsonPath("$.cor").value("Preto"));
    }

    @Test
    void deveDeletarCarro() throws Exception {
        var dto = new CarroRequest(clienteId, "EUD-8679", "Corsa", "Fuxia");
        var salvoJson = mockMvc.perform(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CarroResponse carroSalvo = objectMapper.readValue(salvoJson, CarroResponse.class);

        mockMvc.perform(delete("/carro/" + carroSalvo.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarCarroPorId() throws Exception {
        var dto = new CarroRequest(clienteId, "EUD-8679", "Corsa", "Fuxia");
        var salvoJson = mockMvc.perform(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CarroResponse carroSalvo = objectMapper.readValue(salvoJson, CarroResponse.class);

        mockMvc.perform(get("/carro/" + carroSalvo.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("EUD-8679"))
                .andExpect(jsonPath("$.modelo").value("Corsa"))
                .andExpect(jsonPath("$.cor").value("Fuxia"));
    }

    @Test
    void deveListarCarros() throws Exception {
        var dto = new CarroRequest(clienteId, "EUD-8679", "Corsa", "Fuxia");
        mockMvc.perform(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/carro"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placa").value("ABC-1234"));
    }
}
