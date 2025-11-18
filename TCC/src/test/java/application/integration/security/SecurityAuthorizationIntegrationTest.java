package application.integration.security;

import com.senai.TCC.TccApplication;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository; // <<< adicionado
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository; // <<< adicionado (se existir)
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.infraestructure.security.UsuarioDetailService;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente; // <<< adicionado (se existir)
import com.senai.TCC.model.entities.Estacionamento; // <<< adicionado (se existir)
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.model.enums.TipoDeUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.random.RandomGenerator;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private ClienteRepository clienteRepository; // <<< usado para criar cliente para os testes

    @Autowired(required = false)
    private EstacionamentoRepository estacionamentoRepository; // <<< opcional — se existir

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UsuarioDetailService usuarioDetailsService;

    private String token;
    private Long donoId;
    private Long clienteId;
    private Long estacionamentoId; // usado para ValorRequest

    @BeforeEach
    void Setup(){
        // data nascimento padrão
        Date birthDate = Date.from(LocalDate.of(1990, 1, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        // === cria DonoEstacionamento (como você já fazia) ===
        String uniqueDonoEmail = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueDonoEmail)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.DONO) // use DONO aqui (você colocava ADMIN antes; ajustar se necessário)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true)
                .build();
        dono = donoRepository.save(dono);
        donoId = dono.getId();

        // === cria um Cliente no banco para os testes de carro ===
        // <<< ATENÇÃO: estou assumindo que existe Cliente.builder() com mesmos campos de Dono.
        // Se sua entidade Cliente tiver outro construtor, adapte aqui.
        Cliente cliente = Cliente.builder()
                .nome("Cliente Teste")
                .email("cliente" + System.currentTimeMillis() + "@gmail.com")
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDate)
                .role(Role.CLIENTE)
                .tipoDeUsuario(TipoDeUsuario.CLIENTE)
                .status(true)
                .build();
        cliente = clienteRepository.save(cliente);
        clienteId = cliente.getId();

        // === (Opcional) cria Estacionamento mínimo para testes de Valor ===
        // Só faça se você tiver Estacionamento entity/repository; se não, remova este bloco.
        if (estacionamentoRepository != null) {
            // <<< ATENÇÃO: adapte a criação se sua entidade Estacionamento for diferente
            Estacionamento est = Estacionamento.builder()
                    .nome("Estacionamento Teste")
                    .endereco("Rua Teste, 100")
                    .CEP("12345-678")
                    .numero("100")
                    .fotoUrl("foto.jpg")
                    .numeroAlvaraDeFuncionamento("000111222")
                    .horaAbertura(java.time.LocalTime.of(8,0))
                    .horaFechamento(java.time.LocalTime.of(22,0))
                    .vagasPreferenciais(5)
                    .maxVagas(50)
                    .numeroDeEscrituraImovel("555666")
                    .dono(dono) // relaciona com o dono criado
                    .build();
            est = estacionamentoRepository.save(est);
            estacionamentoId = est.getId();
        } else {
            // se não tiver repository, use donoId como fallback (algumas implementações usam mesmo id)
            estacionamentoId = donoId;
        }

        // token para uso genérico (não usado em todos os testes)
        token = jwtService.generateToken(dono.getEmail(), dono.getRole().name());
    }

    private void mockUsuario(String email, String role) {
        UserDetails user = User.withUsername(email)
                .password("encoded")
                .roles(role)
                .build();
        when(usuarioDetailsService.loadUserByUsername(email)).thenReturn(user);
    }

    // ============================================================
    // ADMIN
    // ============================================================

    @Test
    void adminDevePoderCriarValor() throws Exception {
        String email = "admin@senai.com";
        mockUsuario(email, "ADMIN");
        String token = jwtService.generateToken(email, "ADMIN");

        String valorJson = """
            {
              "tipoDeCobranca": "PORHORA",
              "tipoDePagamento": "DINHEIRO",
              "preco": 10.0,
              "periodo": "FINALDESEMANA",
              "estacionamentoId": %d
            }
            """.formatted(estacionamentoId);

        mockMvc.perform(post("/valor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valorJson))
                .andExpect(status().isCreated());
    }

    @Test
    void adminNaoDeveSerNegadoEmNenhumaRota() throws Exception {
        String email = "admin@senai.com";
        mockUsuario(email, "ADMIN");
        String token = jwtService.generateToken(email, "ADMIN");

        mockMvc.perform(get("/carro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    // ============================================================
    // DONO
    // ============================================================

    @Test
    void donoDevePoderCriarEstacionamento() throws Exception {
        String email = "dono@senai.com";
        mockUsuario(email, "DONO");
        String token = jwtService.generateToken(email, "DONO");

        // JSON completo conforme EstacionamentoRequest record (evita campos null)
        String estacionamentoJson = """
            {
              "nome": "Estacionamento Teste",
              "endereco": "Rua das Flores, 123",
              "CEP": "12345-678",
              "numero": "123",
              "foto": "foto.jpg",
              "numeroAlvaraDeFuncionamento": "123456789",
              "horaFechamento": "22:00:00",
              "horaAbertura": "08:00:00",
              "vagasPreferenciais": 5,
              "maximoDeVagas": 100,
              "numeroDeEscrituraImovel": "987654321"
            }
            """;

        mockMvc.perform(
                post("/estacionamento/" + donoId)
                        .header("Authorization", "Bearer "+token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estacionamentoJson)
        ).andExpect(status().isCreated());
    }

    @Test
    void donoNaoDevePoderPostarCarro() throws Exception {
        String email = "dono@senai.com";
        mockUsuario(email, "DONO");
        String token = jwtService.generateToken(email, "DONO");

        // Mesmo que esteja autenticado como DONO, postar carro deve ser proibido
        String carroJson = """
            {
              "clienteId": %d,
              "placa": "TEST-1234",
              "modelo": "TesteModelo",
              "cor": "Preto"
            }
            """.formatted(clienteId);

        mockMvc.perform(post("/carro")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carroJson))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // GERENTE
    // ============================================================

    @Test
    void gerenteNaoDevePoderCriarEstacionamento() throws Exception {
        String email = "gerente@senai.com";
        mockUsuario(email, "GERENTE");
        String token = jwtService.generateToken(email, "GERENTE");

        String estacionamentoJson = """
            {
              "nome": "Novo",
              "endereco": "Rua X, 1",
              "CEP": "00000-000",
              "numero": "1",
              "foto": "x.jpg",
              "numeroAlvaraDeFuncionamento": "111222333",
              "horaFechamento": "22:00:00",
              "horaAbertura": "08:00:00",
              "vagasPreferenciais": 2,
              "maximoDeVagas": 20,
              "numeroDeEscrituraImovel": "000111"
            }
            """;

        mockMvc.perform(post("/estacionamento/" + donoId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estacionamentoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void gerenteNaoDevePoderConsultarGerente() throws Exception {
        String email = "gerente@senai.com";
        mockUsuario(email, "GERENTE");
        String token = jwtService.generateToken(email, "GERENTE");

        // GET gerente só é permitido para DONO e ADMIN
        mockMvc.perform(get("/gerente")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // CLIENTE
    // ============================================================

    @Test
    void clienteDevePoderCriarCarro() throws Exception {
        String email = "cliente@senai.com";
        mockUsuario(email, "CLIENTE");
        String token = jwtService.generateToken(email, "CLIENTE");
        String placa = "ABC" + System.currentTimeMillis() % 10000;


        // Aqui é importante enviar clienteId (não podia ser nulo)
        String carroJson = """
            {
              "clienteId": %d,
              "placa": "%s",
              "modelo": "Fiesta",
              "cor": "Azul"
            }
            """.formatted(clienteId,placa);

        mockMvc.perform(post("/carro")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carroJson))
                .andExpect(status().isCreated());
    }

    @Test
    void clienteNaoDevePoderCriarValor() throws Exception {
        String email = "cliente@senai.com";
        mockUsuario(email, "CLIENTE");
        String token = jwtService.generateToken(email, "CLIENTE");

        String valorJson = """
            {
              "tipoDeCobranca": "PORHORA",
              "tipoDePagamento": "DINHEIRO",
              "preco": 15.0,
              "periodo": "FINALDESEMANA",
              "estacionamentoId": %d
            }
            """.formatted(estacionamentoId);

        mockMvc.perform(post("/valor")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valorJson))
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // SEM TOKEN
    // ============================================================

    @Test
    void deveNegarAcessoSemToken() throws Exception {
        mockMvc.perform(get("/carro"))
                .andExpect(status().isForbidden());
    }
}
