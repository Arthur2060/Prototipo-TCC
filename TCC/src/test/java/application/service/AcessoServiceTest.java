package application.service;

import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.dto.response.AcessoResponse;
import com.senai.TCC.application.services.AcessoService;
import com.senai.TCC.infraestructure.repositories.AcessoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.model.entities.Acesso;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AcessoServiceTest {

    @Mock
    private AcessoRepository acessoRepository;
    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @InjectMocks
    private AcessoService acessoService;

    private Estacionamento estacionamento;
    private Acesso acesso;
    private AcessoRequest acessoRequest;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setAcessos(new ArrayList<>());

        acesso = new Acesso();
        acesso.setId(1L);
        acesso.setPlacaDoCarro("ABC1234");
        acesso.setHoraDeEntrada(Time.valueOf("08:00:00"));
        acesso.setHoraDeSaida(Time.valueOf("10:00:00"));
        acesso.setValorAPagar(20.0);
        acesso.setStatus(true);
        acesso.setEstacionamento(estacionamento);

        acessoRequest = new AcessoRequest(
                1L,
                "ABC1234",
                Time.valueOf("08:00:00"),
                Time.valueOf("10:00:00"),
                20.0,
                1L
        );
    }

    @Test
    void deveListarAcessos() {
        when(acessoRepository.findByStatusTrue()).thenReturn(List.of(acesso));
        List<AcessoResponse> lista = acessoService.listarAcessos();
        assertEquals(1, lista.size());
        assertEquals("ABC1234", lista.get(0).placaDoCarro());
        verify(acessoRepository).findByStatusTrue();
    }

    @Test
    void deveBuscarAcessoPorId() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(acesso));
        AcessoResponse response = acessoService.buscarPorId(1L);
        assertEquals("ABC1234", response.placaDoCarro());
        verify(acessoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdNaoCadastrado() {
        when(acessoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.buscarPorId(2L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveCadastrarAcesso() {
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(acessoRepository.save(any(Acesso.class))).thenReturn(acesso);

        AcessoResponse response = acessoService.cadastrarAcesso(acessoRequest);
        assertEquals("ABC1234", response.placaDoCarro());
        assertTrue(response.status());
        verify(acessoRepository).save(any(Acesso.class));
    }

    @Test
    void deveLancarExcecaoAoCadastrarComEstacionamentoInexistente() {
        when(estacionamentoRepository.findById(2L)).thenReturn(Optional.empty());
        AcessoRequest req = new AcessoRequest(
                1L, "ABC1234", Time.valueOf("08:00:00"), Time.valueOf("10:00:00"), 20.0, 2L
        );
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.cadastrarAcesso(req));
        assertEquals("Id do estacionamento não encontrado no sistema", ex.getMessage());
    }

    @Test
    void deveAtualizarAcesso() {
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(acesso));
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(acessoRepository.save(any(Acesso.class))).thenReturn(acesso);

        AcessoResponse response = acessoService.atualizarAcesso(acessoRequest, 1L);
        assertEquals("ABC1234", response.placaDoCarro());
        verify(acessoRepository).save(any(Acesso.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarAcessoOuEstacionamentoInexistente() {
        when(acessoRepository.findById(2L)).thenReturn(Optional.empty());
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.atualizarAcesso(acessoRequest, 2L));
        assertEquals("O Acesso ou estacionamento buscado não existe no sistema", ex.getMessage());
    }

    @Test
    void deveDeletarAcesso() {
        estacionamento.getAcessos().add(acesso);
        when(acessoRepository.findById(1L)).thenReturn(Optional.of(acesso));
        when(acessoRepository.save(any(Acesso.class))).thenReturn(acesso);

        acessoService.deletarAcesso(1L);

        assertFalse(acesso.getStatus());
        assertFalse(estacionamento.getAcessos().contains(acesso));
        verify(acessoRepository).save(acesso);
    }

    @Test
    void deveLancarExcecaoAoDeletarAcessoInexistente() {
        when(acessoRepository.findById(2L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> acessoService.deletarAcesso(2L));
        assertEquals("Acesso buscado não cadastrado no sistema", ex.getMessage());
    }
}