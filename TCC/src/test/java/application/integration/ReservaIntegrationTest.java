package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.ReservaRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.ReservaResponse;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.ReservaRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.model.enums.StatusReserva;
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
public class ReservaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String clienteToken;
    private Long clienteId;
    private Long estacionamentoId;

    @BeforeEach
    void setup() throws Exception {
        reservaRepository.deleteAll();
        estacionamentoRepository.deleteAll();
        clienteRepository.deleteAll();
        donoRepository.deleteAll();

        // 1. Criação do Dono e Estacionamento (para criar o EstacionamentoId)
        Date birthDateDono = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmailDono = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueEmailDono)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDateDono)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE) // Assumindo que o Dono também é cliente para fins de login
                .status(true)
                .build();

        donoRepository.save(dono);

        // Criar estacionamento via Request
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

        var responseEstacionamento = mockMvc.perform(withAuth(post("/estacionamento/" + dono.getId()), jwtService.generateToken(dono.getEmail(), dono.getRole().name()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(responseEstacionamento, EstacionamentoResponse.class);
        estacionamentoId = estacionamentoResponse.id();

        // 2. Criação do Cliente (para criar o ClienteId e Token)
        Date birthDateCliente = Date.from(LocalDate.of(1995, 5, 5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmailCliente = "cliente" + System.currentTimeMillis() + "@teste.com";
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email(uniqueEmailCliente)
                .senha(passwordEncoder.encode("senha456"))
                .dataNascimento(birthDateCliente)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();

        clienteRepository.save(cliente);
        clienteId = cliente.getId();
        clienteToken = jwtService.generateToken(cliente.getEmail(), cliente.getRole().name());
    }

    private ReservaRequest reservaRequestValido() {
        return new ReservaRequest(
                clienteId,
                estacionamentoId,
                new Date(System.currentTimeMillis() + 86400000), // amanhã
                Time.valueOf("14:30:00"),
                StatusReserva.PENDENTE
        );
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder, String token) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + clienteToken);
    }

    @Test
    void deveCadastrarReservaValida() throws Exception {
        mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusReserva").value("PENDENTE"));
    }

    @Test
    void deveAtualizarReserva() throws Exception {
        var response = mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        ReservaResponse reservaSalva = objectMapper.readValue(response.getResponse().getContentAsString(), ReservaResponse.class);

        var atualizado = new ReservaRequest(
                clienteId,
                estacionamentoId,
                new Date(System.currentTimeMillis() + 172800000), // dois dias depois
                Time.valueOf("15:00:00"),
                StatusReserva.ACEITA
        );

        mockMvc.perform(withAuth(put("/reserva/" + reservaSalva.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusReserva").value("ACEITA"))
                .andExpect(jsonPath("$.horaDaReserva").value("15:00:00"));
    }

    @Test
    void deveDeletarReserva() throws Exception {
        var response = mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        ReservaResponse reservaSalva = objectMapper.readValue(response.getResponse().getContentAsString(), ReservaResponse.class);

        mockMvc.perform(withAuth(delete("/reserva/" + reservaSalva.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarReservaPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated())
                .andReturn();

        ReservaResponse reservaSalva = objectMapper.readValue(response.getResponse().getContentAsString(), ReservaResponse.class);

        mockMvc.perform(withAuth(get("/reserva/" + reservaSalva.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reservaSalva.id()));
    }

    @Test
    void deveListarReservas() throws Exception {
        mockMvc.perform(withAuth(post("/reserva")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reservaRequestValido()))))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/reserva")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void deveRetornarErroAoBuscarReservaInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/reserva/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarReservaInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/reserva/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID de reserva buscado não encontrado no sistema!"));
    }
}