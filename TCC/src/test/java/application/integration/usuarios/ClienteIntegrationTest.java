package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class ClienteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;

    private ClienteRequest testClienteRequest;

    @BeforeEach
    void setup() {
        clienteRepository.deleteAll();

        Date birthDate = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmail = "pedro" + System.currentTimeMillis() + "@gmail.com";
        Cliente cliente = Cliente.builder()
                .nome("Pedro")
                .email(uniqueEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();


        clienteRepository.save(cliente);

        // reusable ClienteRequest
        testClienteRequest = new ClienteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                birthDate
        );

        token = jwtService.generateToken(cliente.getEmail(), cliente.getRole().name());
    }


    // Helper method to add Authorization header
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    @Test
    void deveCadatrarClienteValido() throws Exception {
        mockMvc.perform(withAuth(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(testClienteRequest))))
                .andExpect(status().isCreated());
    }

    @Test
    void deveAtualizarUsuario() throws Exception {

        var savedResponse = mockMvc.perform(withAuth(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testClienteRequest))))
                .andReturn().getResponse().getContentAsString();

        var usuarioSalvo = objectMapper.readValue(savedResponse, ClienteResponse.class);

        var atualizado = new ClienteRequest(
                "Lucas Updated",
                "lucasupdated@gmail.com",
                "123456",
                new Date()
        );

        mockMvc.perform(withAuth(put("/cliente/" + usuarioSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lucas Updated"));
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        var savedResponse = mockMvc.perform(withAuth(post("/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testClienteRequest))))
                .andReturn().getResponse().getContentAsString();

        var usuario = objectMapper.readValue(savedResponse, ClienteResponse.class);

        mockMvc.perform(withAuth(delete("/cliente/" + usuario.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroAoDeletarUsuarioInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/cliente/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Cliente buscado não cadastrado no sistema"));
    }
}