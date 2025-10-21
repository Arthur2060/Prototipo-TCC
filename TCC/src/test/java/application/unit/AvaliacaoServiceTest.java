package application.unit;

import com.senai.TCC.application.dto.requests.AvaliacaoRequest;
import com.senai.TCC.application.dto.response.AvaliacaoResponse;

import com.senai.TCC.application.services.AvaliacaoService;
import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import com.senai.TCC.model.service.ValidadorAvaliacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;
    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private ValidadorAvaliacao validador;
    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private Avaliacao avaliacao;
    private AvaliacaoRequest avaliacaoRequest;
    private Estacionamento estacionamento;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setAvaliacoes(new ArrayList<>());

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setAvaliacoes(new ArrayList<>());

        avaliacao = new Avaliacao();
        avaliacao.setId(1L);
        avaliacao.setNota((short) 4);
        avaliacao.setComentario("Muito bom");
        avaliacao.setDataDeAvaliacao(LocalDateTime.now());
        avaliacao.setStatus(true);
        avaliacao.setEstacionamento(estacionamento);
        avaliacao.setCliente(cliente);

        avaliacaoRequest = new AvaliacaoRequest(
                1L,
                1L,
                (short) 4,
                "Muito bom",
                LocalDateTime.now()
        );
    }

    @Test
    void deveListarAvaliacoes() {
        when(avaliacaoRepository.findByStatusTrue()).thenReturn(List.of(avaliacao));
        List<AvaliacaoResponse> lista = avaliacaoService.listarAvaliacoes();
        assertEquals(1, lista.size());
        assertEquals((short) 4, lista.get(0).nota());
        verify(avaliacaoRepository).findByStatusTrue();
    }

    @Test
    void deveBuscarAvaliacaoPorId() {
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        AvaliacaoResponse response = avaliacaoService.buscarPorId(1L);
        assertEquals((short) 4, response.nota());
        verify(avaliacaoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(avaliacaoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> avaliacaoService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarAvaliacao() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        AvaliacaoResponse response = avaliacaoService.cadastrarAvaliacao(avaliacaoRequest);
        assertEquals((short) 4, response.nota());
        assertTrue(response.status());
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoClienteNaoExiste() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IdNaoCadastrado.class, () ->
                avaliacaoService.cadastrarAvaliacao(avaliacaoRequest)
        );
        verify(estacionamentoRepository, never()).findById(anyLong());
    }

    @Test
    void deveLancarExcecaoQuandoEstacionamentoNaoExiste() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(new Cliente()));
        when(estacionamentoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IdNaoCadastrado.class, () ->
                avaliacaoService.cadastrarAvaliacao(avaliacaoRequest)
        );
    }

    @Test
    void deveAtualizarAvaliacao() {
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        AvaliacaoRequest req = new AvaliacaoRequest(1L, 1L, (short) 5, "Excelente", LocalDateTime.now());
        AvaliacaoResponse response = avaliacaoService.atualizarAvaliacao(req, 1L);
        assertEquals((short) 5, response.nota());
        assertEquals("Excelente", response.comentario());
        verify(avaliacaoRepository).save(any(Avaliacao.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarAvaliacaoInexistente() {
        when(avaliacaoRepository.findById(2L)).thenReturn(Optional.empty());
        AvaliacaoRequest req = new AvaliacaoRequest(1L, 1L, (short) 5, "Excelente", LocalDateTime.now());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> avaliacaoService.atualizarAvaliacao(req, 2L));
        assertEquals("A avaliação buscada não existe no sistema", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComClienteOuEstacionamentoInexistente() {
        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        when(clienteRepository.findById(2L)).thenReturn(Optional.empty());
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        AvaliacaoRequest req = new AvaliacaoRequest(2L, 1L, (short) 5, "Excelente", LocalDateTime.now());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> avaliacaoService.atualizarAvaliacao(req, 1L));
        assertEquals("Cliente ou estacionamento não encontrado no sistema", ex.getMessage());
    }

    @Test
    void deveDeletarAvaliacao() {
        estacionamento.getAvaliacoes().add(avaliacao);
        cliente.getAvaliacoes().add(avaliacao);

        when(avaliacaoRepository.findById(1L)).thenReturn(Optional.of(avaliacao));
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenReturn(avaliacao);

        avaliacaoService.deletarAvaliacao(1L);

        assertFalse(avaliacao.getStatus());
        assertFalse(estacionamento.getAvaliacoes().contains(avaliacao));
        assertFalse(cliente.getAvaliacoes().contains(avaliacao));
        verify(avaliacaoRepository).save(avaliacao);
    }

    @Test
    void deveLancarExcecaoAoDeletarAvaliacaoInexistente() {
        when(avaliacaoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> avaliacaoService.deletarAvaliacao(2L));
        assertEquals("Não foi possivel encontrar a avalição buscada", ex.getMessage());
    }
}