package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.ValorRequest;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.application.dto.response.ValorResponse;
import com.senai.TCC.infraestructure.repositories.ValorRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class ValorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ValorRepository valorRepository;

    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;
    private Long estacionamentoId;
    private DonoRequest testDonoRequest;

    @BeforeEach
    void setUp() throws Exception {
        valorRepository.deleteAll();
        donoRepository.deleteAll();

        Date birthDate = Date.from(LocalDate.of(2000, 9, 12)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "pedro" + System.currentTimeMillis() + "@gmail.com";

        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();

        donoRepository.save(dono);

        // reusable DonoRequest
        testDonoRequest = new DonoRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                birthDate
        );

        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());

        // Criação de Estacionamento
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

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder request) {
        return request.header("Authorization", "Bearer " + token);
    }

    private ValorRequest criarValorRequest(Double preco) {
        return new ValorRequest(Cobranca.PORHORA, Metodo.DINHEIRO, preco, Periodo.FINALDESEMANA, estacionamentoId);
    }

    @Test
    void deveCadastrarValorValido() throws Exception {
        var dto = criarValorRequest(10.0);

        mockMvc.perform(withAuth(post("/valor"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.preco").value(10.0));
    }

    @Test
    void deveAtualizarValor() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvoJson = mockMvc.perform(withAuth(post("/valor"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var valorSalvo = objectMapper.readValue(salvoJson, ValorResponse.class);

        var atualizado = new ValorRequest(Cobranca.PORVAGA, Metodo.PIX, 20.0, Periodo.FINALDESEMANA, estacionamentoId);
        mockMvc.perform(withAuth(put("/valor/" + valorSalvo.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(20.0))
                .andExpect(jsonPath("$.tipoDeCobranca").value("PORVAGA"))
                .andExpect(jsonPath("$.tipoDePagamento").value("PIX"));
    }

    @Test
    void deveDeletarValor() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvo = mockMvc.perform(withAuth(post("/valor"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var valorSalvo = objectMapper.readValue(salvo, ValorResponse.class);

        mockMvc.perform(withAuth(delete("/valor/" + valorSalvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarValorPorId() throws Exception {
        var dto = criarValorRequest(10.0);
        var salvo = mockMvc.perform(withAuth(post("/valor"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var valorSalvo = objectMapper.readValue(salvo, ValorResponse.class);

        mockMvc.perform(withAuth(get("/valor/" + valorSalvo.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preco").value(10.0));
    }

    @Test
    void deveListarValores() throws Exception {
        var dto = criarValorRequest(10.0);
        mockMvc.perform(withAuth(post("/valor"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/valor")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].preco").value(10.0));
    }
}
