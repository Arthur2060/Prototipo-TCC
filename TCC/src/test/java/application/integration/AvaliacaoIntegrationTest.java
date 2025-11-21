package application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senai.TCC.TccApplication;
import com.senai.TCC.application.dto.requests.AvaliacaoRequest;
import com.senai.TCC.application.dto.response.AvaliacaoResponse;
import com.senai.TCC.infraestructure.repositories.*;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.infraestructure.security.JwtService;
import com.senai.TCC.model.entities.Carro;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.Reserva;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.enums.Role;
import com.senai.TCC.model.enums.StatusReserva;
import com.senai.TCC.model.enums.TipoDeUsuario;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TccApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AvaliacaoIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private DonoRepository donoRepository;
    @Autowired private EstacionamentoRepository estacionamentoRepository;
    @Autowired private AvaliacaoRepository avaliacaoRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private AcessoRepository acessoRepository;
    @Autowired private CarroRepository carroRepository;
    @Autowired private ReservaRepository reservaRepository;

    private String clienteToken;
    private Long clienteId;
    private Long estacionamentoId;

    @BeforeEach
    void setup() throws Exception {
        // Limpeza (mantendo sua ordem)
        avaliacaoRepository.deleteAll();
        estacionamentoRepository.deleteAll();
        clienteRepository.deleteAll();
        donoRepository.deleteAll();

        // 1) Dono
        Date birthDateDono = Date.from(LocalDate.of(1990,1,1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant());

        String uniqueEmailDono = "dono" + System.currentTimeMillis() + "@gmail.com";
        DonoEstacionamento dono = DonoEstacionamento.builder()
                .nome("Pedro Dono")
                .email(uniqueEmailDono)
                .senha(passwordEncoder.encode("123456"))
                .dataNascimento(birthDateDono)
                .role(Role.ADMIN)
                .tipoDeUsuario(TipoDeUsuario.DONO)
                .status(true) // se existir este campo no builder
                .build();

        donoRepository.save(dono);

        Estacionamento estacionamento = Estacionamento.builder()
                .nome("Estacionamento Teste")
                .endereco("Rua Mockada, 123")
                .CEP("12345-678")
                .numero("123")
                .fotoUrl(null)
                .numeroAlvaraDeFuncionamento("123456789")
                .horaAbertura(LocalTime.of(7, 0))
                .horaFechamento(LocalTime.of(22, 0))
                .maxVagas(50)
                // other builder fields you want...
                .dono(dono)
                .build();

        estacionamento.setStatus(true);
        if (estacionamento.getAvaliacoes() == null) {
            estacionamento.setAvaliacoes(new ArrayList<>());
        }
        if (estacionamento.getReservas() == null) {
            estacionamento.setReservas(new ArrayList<>());
        }
        if (estacionamento.getGerentes() == null) {
            estacionamento.setGerentes(new ArrayList<>());
        }

        if (estacionamento.getMaxVagas() != null) {
            estacionamento.setVagasDisponiveis(estacionamento.getMaxVagas());
        }

        estacionamentoRepository.saveAndFlush(estacionamento);
        estacionamentoId = estacionamento.getId();

        System.out.println(">>> saved estacionamento.id = " + estacionamentoId);
        System.out.println(">>> findById present? " + estacionamentoRepository.findById(estacionamentoId).isPresent());


        // 3) Cliente (igual ao que você já fazia)
        Date birthDateCliente = Date.from(LocalDate.of(1995,5,5)
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

        Carro carro = null;
        try {
            // try builder if available
            carro = Carro.builder()
                    .placa("ABC1234")
                    .modelo("Teste")
                    .cor("Teste")
                    .cliente(cliente)
                    .build();
        } catch (Throwable ignored) {
            // fallback to constructor + setters if there's no builder
            carro = new Carro();
            try { carro.setPlaca("ABC1234"); } catch (Throwable t) {}
            try { carro.setCor("Teste"); } catch (Throwable t) {}
            try { carro.setModelo("Teste"); } catch (Throwable t) {}
            try { carro.setCliente(cliente); } catch (Throwable t) {}
        }

        try {
            if (cliente.getCarros() == null) cliente.setCarros(new java.util.ArrayList<>());
            cliente.getCarros().add(carro);
        } catch (Throwable ignored) {}

        carro = carroRepository.saveAndFlush(carro);

        Reserva reserva = null;
        try {
            reserva = Reserva.builder()
                    .cliente(cliente)
                    .estacionamento(estacionamento)
                    .dataDaReserva(new java.util.Date())
                    .statusReserva(StatusReserva.PENDENTE)
                    .build();
        } catch (Throwable ignored) {
            reserva = new Reserva();
            try { reserva.setCliente(cliente); } catch (Throwable t) {}
            try { reserva.setEstacionamento(estacionamento); } catch (Throwable t) {}
            try { reserva.setDataDaReserva(new java.util.Date()); } catch (Throwable t) {}
            try { reserva.setStatusReserva(StatusReserva.PENDENTE); } catch (Throwable t) {}
        }

        try {
            if (cliente.getReservas() == null) cliente.setReservas(new java.util.ArrayList<>());
            cliente.getReservas().add(reserva);
        } catch (Throwable ignored) {}

        reserva = reservaRepository.saveAndFlush(reserva);

        com.senai.TCC.model.entities.Acesso acesso = new com.senai.TCC.model.entities.Acesso();
        try { acesso.setCarro(carro); } catch (Throwable ignored) {}
        try { acesso.setEstacionamento(estacionamento); } catch (Throwable ignored) {}
        try { acesso.setPlacaDoCarro(carro.getPlaca()); } catch (Throwable ignored) {}
        java.time.LocalTime entrada = java.time.LocalTime.now().minusHours(1);
        java.time.LocalTime saida = java.time.LocalTime.now();
        try { acesso.setHoraDeEntrada((entrada)); } catch (Throwable ignored) {}
        try { acesso.setHoraDeSaida((saida)); } catch (Throwable ignored) {}
        try { acesso.setTotalHoras((int) java.time.Duration.between(entrada, saida).toHours()); } catch (Throwable ignored) {}
        try { acesso.setValorAPagar(5.0); } catch (Throwable ignored) {}
        try { acesso.setStatus(true); } catch (Throwable ignored) {}

        try {
            if (carro.getAcessos() == null) carro.setAcessos(new java.util.ArrayList<>());
            carro.getAcessos().add(acesso);
        } catch (Throwable ignored) {}

        acesso = acessoRepository.saveAndFlush(acesso);

        try { carroRepository.saveAndFlush(carro); } catch (Throwable ignored) {}
        try { clienteRepository.saveAndFlush(cliente); } catch (Throwable ignored) {}
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

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder withAuth(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder) {
        return builder.header("Authorization", "Bearer " + clienteToken);
    }

    @Test
    void deveCadastrarAvaliacaoValida() throws Exception {
        mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cliente.id").value(clienteId))
                .andExpect(jsonPath("$.estacioId").value(estacionamentoId))
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentario").value("Muito bom!"));
    }

    @Test
    void deveAtualizarAvaliacao() throws Exception {
        var response = mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andReturn();

        AvaliacaoResponse avaliacaoSalva = objectMapper.readValue(response.getResponse().getContentAsString(), AvaliacaoResponse.class);

        var atualizado = new AvaliacaoRequest(
                clienteId,
                estacionamentoId,
                (short) 5,
                "Excelente! (Atualizado)",
                LocalDateTime.now()
        );

        mockMvc.perform(withAuth(put("/avaliacao/" + avaliacaoSalva.id()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(atualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(avaliacaoSalva.id()))
                .andExpect(jsonPath("$.nota").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente! (Atualizado)"));
    }

    @Test
    void deveDeletarAvaliacao() throws Exception {
        var response = mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andReturn();

        AvaliacaoResponse avaliacaoSalva = objectMapper.readValue(response.getResponse().getContentAsString(), AvaliacaoResponse.class);

        mockMvc.perform(withAuth(delete("/avaliacao/" + avaliacaoSalva.id())))
                .andExpect(status().isNoContent());

        mockMvc.perform(withAuth(get("/avaliacao/" + avaliacaoSalva.id())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveBuscarAvaliacaoPorId() throws Exception {
        var response = mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated())
                .andReturn();

        AvaliacaoResponse avaliacaoSalva = objectMapper.readValue(response.getResponse().getContentAsString(), AvaliacaoResponse.class);

        mockMvc.perform(withAuth(get("/avaliacao/" + avaliacaoSalva.id())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(avaliacaoSalva.id()))
                .andExpect(jsonPath("$.nota").value(4))
                .andExpect(jsonPath("$.comentario").value("Muito bom!"));
    }

    @Test
    void deveListarAvaliacoes() throws Exception {
        mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(avaliacaoRequestValida())))
                .andExpect(status().isCreated());

        var segundaAvaliacao = new AvaliacaoRequest(
                clienteId,
                estacionamentoId,
                (short) 5,
                "Segunda avaliação",
                LocalDateTime.now()
        );

        mockMvc.perform(withAuth(post("/avaliacao"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(segundaAvaliacao)))
                .andExpect(status().isCreated());

        mockMvc.perform(withAuth(get("/avaliacao")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].nota").value(4))
                .andExpect(jsonPath("$[1].nota").value(5));
    }

    @Test
    void deveRetornarErroAoBuscarAvaliacaoInexistente() throws Exception {
        mockMvc.perform(withAuth(get("/avaliacao/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("ID buscado não foi encontrado no sistema!"));
    }

    @Test
    void deveRetornarErroAoDeletarAvaliacaoInexistente() throws Exception {
        mockMvc.perform(withAuth(delete("/avaliacao/9999")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.erro").value("Não foi possivel encontrar a avalição buscada"));
    }
}
