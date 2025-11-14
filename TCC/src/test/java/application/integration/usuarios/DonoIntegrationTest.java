package application.integration.usuarios;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.dto.requests.CarroRequest;
import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.CarroResponse;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.infraestructure.repositories.AcessoRepository;
import com.senai.TCC.infraestructure.repositories.CarroRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
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

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
public class DonoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DonoRepository donoRepository;
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;
    @Autowired
    private AcessoRepository acessoRepository;
    @Autowired
    private CarroRepository carroRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;

    private DonoRequest testDonoRequest;

    private Long estacionamentoId;
    private Long carroId; // Será populado dinamicamente com o ID do carro criado no setup
    private Cliente testClient; // Armazena o cliente criado para uso posterior, se necessário

    @BeforeEach
    void setup() throws Exception {
        // 1. Limpeza do banco de dados na ordem correta (entidades filhas antes das pais)
        // Isso garante que não haja violações de chave estrangeira durante a limpeza.
        acessoRepository.deleteAllInBatch();        // Limpa Acessos (filhos de Carro e Estacionamento)
        carroRepository.deleteAllInBatch();         // Limpa Carros (filhos de Cliente)
        estacionamentoRepository.deleteAllInBatch();// Limpa Estacionamentos (filhos de DonoEstacionamento)
        donoRepository.deleteAllInBatch();          // Limpa DonoEstacionamentos (subclasse de Usuario)
        clienteRepository.deleteAllInBatch();       // Limpa Clientes (subclasse de Usuario)
        // Nota: Se houver uma tabela base 'Usuario' concreta e não abstrata, e ela não for limpa
        // automaticamente pelas subclasses, você precisaria de um 'UsuarioRepository' e chamá-lo aqui por último.

        // 2. Criação do DonoEstacionamento (para autenticação e criação de estacionamento)
        Date birthDate = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Gera um email único para evitar conflitos em execuções paralelas ou consecutivas
        String uniqueDonoEmail = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueDonoEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();
        donoRepository.save(dono);

        // 3. Gerar token JWT para o Dono recém-criado
        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());

        // 4. Criar Estacionamento via endpoint, associado ao Dono
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

        // Executa a requisição e extrai o ID do estacionamento da resposta
        String responseEstacionamentoString = mockMvc.perform(withAuth(post("/estacionamento/" + dono.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(estacionamentoRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EstacionamentoResponse estacionamentoResponse = objectMapper.readValue(responseEstacionamentoString, EstacionamentoResponse.class);
        this.estacionamentoId = estacionamentoResponse.id(); // Popula o ID do estacionamento para uso nos testes

        // 5. Criação Cliente
        String emailCliente = "cliente" + System.currentTimeMillis() + "@gmail.com";
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email(emailCliente)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.CLIENTE)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();
        testClient = clienteRepository.save(cliente); // Salva o cliente e atribui ao campo testClient

        // 6. Criar Carro via endpoint, associado ao Cliente
        var carroRequest = new CarroRequest(
                testClient.getId(), // Usa o ID do cliente criado neste setup
                "ABC1234",
                "Modelo X",
                "Cor Vermelha"
        );
        // Executa a requisição e extrai o ID do carro da resposta
        String responseCarroString = mockMvc.perform(withAuth(post("/carro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(carroRequest))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CarroResponse carroResponse = objectMapper.readValue(responseCarroString, CarroResponse.class);
        this.carroId = carroResponse.id(); // Popula o ID do carro para uso nos testes de Acesso
    }

    /**
     * Cria um AcessoRequest válido utilizando os IDs de Carro e Estacionamento
     * que foram dinamicamente criados e populados no método setup().
     * @return AcessoRequest configurado.
     */
    private AcessoRequest acessoRequestValido() {
        return new AcessoRequest(
                this.carroId, // Agora usa o carroId populado dinamicamente, garantindo consistência
                "ABC1234",
                Time.valueOf("08:00:00"),
                Time.valueOf("10:00:00"),
                20.0,
                estacionamentoId
        );
    }

    // Adiciona Authorization header
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + token);
    }

    void deveCadastrarDonoValido() throws Exception {
        String uniqueEmail = "dono.novo." + UUID.randomUUID() + "@gmail.com";
        DonoRequest novoDonoRequest = new DonoRequest(
                "Novo Dono Teste",
                uniqueEmail,
                "senha123",
                new Date()
        );

        mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value(novoDonoRequest.nome()))
                .andExpect(jsonPath("$.email").value(novoDonoRequest.email()))
                .andExpect(jsonPath("$.dataNascimento").exists());
    }

    @Test
    void deveAtualizarDono() throws Exception {
        // Cria um novo Dono para o teste de atualização
        var novoDonoRequest = new DonoRequest(
                "Dono para Atualizar",
                "dono.update" + System.currentTimeMillis() + "@gmail.com",
                "senha123",
                new Date()
        );

        var savedResponse = mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var donoSalvo = objectMapper.readValue(savedResponse, DonoResponse.class);

        var atualizado = new DonoRequest(
                "Carlos Atualizado",
                "carlos.atualizado@gmail.com",
                "654321",
                new Date()
        );

        mockMvc.perform(withAuth(put("/dono/" + donoSalvo.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(atualizado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Atualizado"));
    }

    @Test
    void deveDeletarDono() throws Exception {
        // Cria um novo Dono para o teste de exclusão
        var novoDonoRequest = new DonoRequest(
                "Dono para Deletar",
                "dono.delete" + System.currentTimeMillis() + "@gmail.com",
                "senha123",
                new Date()
        );

        var savedResponse = mockMvc.perform(withAuth(post("/dono")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoDonoRequest))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        var donoSalvo = objectMapper.readValue(savedResponse, DonoResponse.class);

        mockMvc.perform(withAuth(delete("/dono/" + donoSalvo.id())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornarErroAoDeletarDonoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/dono/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Dono buscado não cadastrado no sistema"));
    }
}
