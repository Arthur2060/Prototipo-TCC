package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.CarroRequest;
import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.CarroResponse;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.infraestructure.repositories.CarroRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.model.enums.TipoDeUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class CarroIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;
    private Long clienteId;

    @BeforeEach
    void setUp() throws Exception {
        // Clear database
        carroRepository.deleteAll();
        clienteRepository.deleteAll();

        // Create test cliente
        Date birthDate = Date.from(LocalDate.of(1995, 5, 5)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "cliente" + System.currentTimeMillis() + "@teste.com";
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("senha456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();

        clienteRepository.save(cliente);
        clienteId = cliente.getId();

        // Generate JWT token
        token = jwtService.generateToken(cliente.getEmail(), cliente.getRole().name());
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    private CarroRequest carroRequestPadrao() {
        return new CarroRequest(clienteId, "EUD-8679", "Corsa", "Fuxia");
    }

    @Test
    void deveCadastrarCarroValido() throws Exception {
        mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.placa").value("EUD-8679"))
                .andExpect(jsonPath("$.modelo").value("Corsa"))
                .andExpect(jsonPath("$.cor").value("Fuxia"));
    }

    @Test
    void deveAtualizarCarro() throws Exception {
        var response = mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andReturn();

        CarroResponse carroSalvo = objectMapper.readValue(response.getResponse().getContentAsString(), CarroResponse.class);

        var atualizado = new CarroRequest(clienteId, "ABC-1234", "Onix", "Preto");

        mockMvc.perform(withAuth(put("/carro/" + carroSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("ABC-1234"))
                .andExpect(jsonPath("$.modelo").value("Onix"))
                .andExpect(jsonPath("$.cor").value("Preto"));
    }

    @Test
    void deveDeletarCarro() throws Exception {
        var response = mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andReturn();

        CarroResponse carroSalvo = objectMapper.readValue(response.getResponse().getContentAsString(), CarroResponse.class);

        mockMvc.perform(withAuth(delete("/carro/" + carroSalvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveBuscarCarroPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated())
                .andReturn();

        CarroResponse carroSalvo = objectMapper.readValue(response.getResponse().getContentAsString(), CarroResponse.class);

        mockMvc.perform(withAuth(get("/carro/" + carroSalvo.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placa").value("EUD-8679"))
                .andExpect(jsonPath("$.modelo").value("Corsa"))
                .andExpect(jsonPath("$.cor").value("Fuxia"));
    }

    @Test
    void deveListarCarros() throws Exception {
        mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequestPadrao()))))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/carro")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].placa").value("EUD-8679"));
    }
}
