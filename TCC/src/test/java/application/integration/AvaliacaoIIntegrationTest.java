package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.application.dto.requests.AvaliacaoRequest;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.AvaliacaoResponse;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.application.services.EstacionamentoService;
import com.senai.TCC.application.services.usuario.ClienteService;
import com.senai.TCC.application.services.usuario.DonoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.senai.TCC.TccApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AvaliacaoIIntegrationTest {
    /*TODO*/
    //Refazer tudo
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private DonoService donoService;

    @Autowired
    private EstacionamentoService estacionamentoService;

    private Long clienteId;
    private Long estacionamentoId;

    @BeforeEach
    void setUp() {
        // Cria um Dono e Estacionamento
        var donoRequest = new DonoRequest("Dono Teste", "dono@teste.com", "senha123", Date.valueOf("1990-01-01"));
        DonoResponse dono = donoService.cadastrarDono(donoRequest);
        var estacionamentoRequest = new EstacionamentoRequest(
                "Estacionamento Teste",
                "Rua B, 456",
                "22222222",
                "30",
                new File("foto2.jpg"),
                "senha123",
                LocalTime.of(7, 0),
                LocalTime.of(23, 0),
                100,
                150,
                "senha123"
        );
        EstacionamentoResponse estacionamento = estacionamentoService.cadastrarEstacionamento(estacionamentoRequest, dono.id());
        estacionamentoId = estacionamento.id();

        // Cria um Cliente
        var clienteRequest = new ClienteRequest("Cliente Teste", "cliente@teste.com", "senha456", Date.valueOf("1995-05-05"));
        ClienteResponse cliente = clienteService.cadastrarCliente(clienteRequest);
        clienteId = cliente.id();
    }

    private AvaliacaoRequest avaliacaoRequestValida() {
        return new AvaliacaoRequest(
                clienteId,
                estacionamentoId,
                (short) 4,
                "Muito bom!",
                LocalDateTime.now()
        );
    }

    @Test
    void deveCadastrarAvaliacaoValida() throws Exception {
        var dto = avaliacaoRequestValida();
        mockMvc.perform(post("/Avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.estacioId").value(estacionamentoId)) // <--- alterado
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentario").value("Muito bom!"));
    }

    @Test
    void deveAtualizarAvaliacao() throws Exception {
        // Cria uma nova avaliação para este teste, garantindo unicidade
        var dto = new AvaliacaoRequest(clienteId, estacionamentoId, (short) 4, "Avaliação a ser atualizada!", LocalDateTime.now());
        var salvo = mockMvc.perform(post("/Avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        var avaliacaoSalva = objectMapper.readValue(salvo, AvaliacaoResponse.class);

        var atualizado = new AvaliacaoRequest(
                clienteId,
                estacionamentoId,
                (short) 5,
                "Excelente!",
                LocalDateTime.now()
        );

        mockMvc.perform(put("/Avaliacao/" + avaliacaoSalva.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(avaliacaoSalva.id()))
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente!"));
    }

    @Test
    void deveDeletarAvaliacao() throws Exception {
        // Cria uma avaliação única para este teste de deleção
        var dto = new AvaliacaoRequest(clienteId, estacionamentoId, (short) 3, "Avaliação a ser deletada.", LocalDateTime.now());
        var salvo = mockMvc.perform(post("/Avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        var avaliacaoSalva = objectMapper.readValue(salvo, AvaliacaoResponse.class);

        mockMvc.perform(delete("/Avaliacao/" + avaliacaoSalva.id()))
                .andExpect(status().isNoContent());

        // Verificação adicional para garantir que foi deletado
        mockMvc.perform(get("/Avaliacao/" + avaliacaoSalva.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarAvaliacaoPorId() throws Exception {
        var dto = avaliacaoRequestValida();
        var salvo = mockMvc.perform(post("/Avaliacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn().getResponse().getContentAsString();
        var avaliacaoSalva = objectMapper.readValue(salvo, AvaliacaoResponse.class);

        mockMvc.perform(get("/Avaliacao/" + avaliacaoSalva.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(avaliacaoSalva.id()))
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentario").value("Muito bom!"));
    }

    @Test
    void deveListarAvaliacoes() throws Exception {
        // Adiciona avaliações para este teste específico, sem depender de outros testes
        mockMvc.perform(post("/Avaliacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AvaliacaoRequest(clienteId, estacionamentoId, (short) 4, "Primeira", LocalDateTime.now()))));

        mockMvc.perform(post("/Avaliacao")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AvaliacaoRequest(clienteId, estacionamentoId, (short) 5, "Segunda", LocalDateTime.now()))));

        mockMvc.perform(get("/Avaliacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nota").value(4))
                .andExpect(jsonPath("$[1].nota").value(5));
    }
}