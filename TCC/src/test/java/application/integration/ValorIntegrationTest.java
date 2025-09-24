package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.ValorRequest;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.application.dto.response.ValorResponse;
import com.senai.TCC.model.enums.Cobranca;
import com.senai.TCC.model.enums.Metodo;
import com.senai.TCC.model.enums.Periodo;
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
public class ValorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long estacionamentoId;

    @BeforeEach
    void setUp() throws Exception {
        // Criar Dono
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

        // Criar Estacionamento
        var estacionamentoRequest = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                null, // foto null para testes
                "123456789",
                LocalTime.of(22, 0),
                LocalTime.of(8, 0),
                10,
                100,
                "987654321"
        );
        var estacionamentoJson = mockMvc.perform(post("/estacionamento/" + donoResponse.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(estacionamentoRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(estacionamentoJson, EstacionamentoResponse.class);
        estacionamentoId = estacionamentoResponse.id();
    }

    private ValorRequest criarValorRequest(Double preco) {
        return new ValorRequest(Cobranca.PORHORA, Metodo.DINHEIRO, preco, Periodo.FINALDESEMANA, estacionamentoId);
    }

    @Test
    void deveCadastrarValorValido() throws Exception {
        var dto = criarValorRequest(10.0);
        mockMvc.perform(post("/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.preco").value(10.0));
    }

    @Test
    void deveAtualizarValor() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvoJson = mockMvc.perform(post("/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var valorSalvo = objectMapper.readValue(salvoJson, ValorResponse.class);

        var atualizado = new ValorRequest(Cobranca.PORVAGA, Metodo.PIX, 20.0, Periodo.FINALDESEMANA, estacionamentoId);
        mockMvc.perform(put("/valor/" + valorSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(20.0))
                .andExpect(jsonPath("$.cobranca").value("PORVAGA"))
                .andExpect(jsonPath("$.metodo").value("PIX"));
    }

    @Test
    void deveDeletarValor() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvo = mockMvc.perform(post("/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var valorSalvo = objectMapper.readValue(salvo, ValorResponse.class);

        mockMvc.perform(delete("/valor/" + valorSalvo.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarValorPorId() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvo = mockMvc.perform(post("/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var valorSalvo = objectMapper.readValue(salvo, ValorResponse.class);

        mockMvc.perform(get("/valor/" + valorSalvo.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(10.0));
    }

    @Test
    void deveListarValores() throws Exception {
        var dto = criarValorRequest(10.0);
        mockMvc.perform(post("/valor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/valor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].preco").value(10.0));
    }
}
