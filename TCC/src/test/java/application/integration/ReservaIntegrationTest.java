package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.ReservaRequest;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.ReservaResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.model.enums.StatusReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.senai.TCC.TccApplication.class)
@AutoConfigureMockMvc
public class ReservaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long estacionamentoId;
    private Long clienteId = 1L; // ajuste conforme necessário

    @BeforeEach
    void setUp() throws Exception {
        // Cria Dono
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

        // Cria Estacionamento
        var estacionamentoRequest = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                null,
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

    private ReservaRequest criarReservaRequest() {
        return new ReservaRequest(
                clienteId,
                estacionamentoId,
                new Date(System.currentTimeMillis() + 86400000), // amanhã
                Time.valueOf("14:30:00"),
                StatusReserva.PENDENTE
        );
    }

    @Test
    void deveCadastrarReservaValida() throws Exception {
        var dto = criarReservaRequest();
        mockMvc.perform(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusReserva").value("PENDENTE"));
    }

    @Test
    void deveAtualizarReserva() throws Exception {
        var dto = criarReservaRequest();
        var salvoJson = mockMvc.perform(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var reservaSalva = objectMapper.readValue(salvoJson, ReservaResponse.class);

        var atualizado = new ReservaRequest(
                clienteId,
                estacionamentoId,
                new Date(System.currentTimeMillis() + 172800000), // dois dias depois
                Time.valueOf("15:00:00"),
                StatusReserva.ACEITA
        );

        mockMvc.perform(put("/reserva/" + reservaSalva.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusReserva").value("ACEITA"))
                .andExpect(jsonPath("$.horaDaReserva").value("15:00:00"));
    }

    @Test
    void deveDeletarReserva() throws Exception {
        var dto = criarReservaRequest();
        var salvoJson = mockMvc.perform(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var reservaSalva = objectMapper.readValue(salvoJson, ReservaResponse.class);

        mockMvc.perform(delete("/reserva/" + reservaSalva.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarReservaPorId() throws Exception {
        var dto = criarReservaRequest();
        var salvoJson = mockMvc.perform(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        var reservaSalva = objectMapper.readValue(salvoJson, ReservaResponse.class);

        mockMvc.perform(get("/reserva/" + reservaSalva.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaSalva.id()));
    }

    @Test
    void deveListarReservas() throws Exception {
        var dto = criarReservaRequest();
        mockMvc.perform(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/reserva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}
