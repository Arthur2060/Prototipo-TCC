package application.unit.usuarios;

import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.application.mappers.usuario.GerenteMapper;
import com.senai.TCC.application.services.usuario.GerenteService;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.GerenteRepository;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GerenteServiceTest {

    @Mock
    private GerenteRepository repository;
    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private GerenteService service;

    private GerenteRequest dto;
    private Estacionamento estacionamento;
    private EstacionamentoRequest estacionamentoRequest;
    private DonoEstacionamento dono;

    @BeforeEach
    void setUp() {
        estacionamento = new Estacionamento();
        estacionamento.setId(1L);
        estacionamento.setNome("EstacioPlay");
        estacionamento.setEndereco("Rua das Flores");
        estacionamento.setCEP("12345-678");
        estacionamento.setNumero("123");
        estacionamento.setFoto("foto.jpg");
        estacionamento.setNumeroAlvaraDeFuncionamento("123456789");
        estacionamento.setHoraFechamento(LocalTime.of(22,0));
        estacionamento.setHoraAbertura(LocalTime.of(8,0));
        estacionamento.setVagasPreferenciais(10);
        estacionamento.setMaxVagas(100);
        estacionamento.setNumeroDeEscrituraImovel("987654321");
        estacionamento.setStatus(true);

        estacionamento.setAvaliacoes(new ArrayList<>());
        estacionamento.setAcessos(new ArrayList<>());
        estacionamento.setReservas(new ArrayList<>());
        estacionamento.setGerentes(new ArrayList<>());

        estacionamentoRequest = new EstacionamentoRequest(
                "EstacioPlay",
                "Rua das Flores",
                "12345-678",
                "123",
                "foto.jpg",
                "123456789",
                LocalTime.of(22,0),
                LocalTime.of(8,0),
                10,
                100,
                "987654321"
        );

        dono = new DonoEstacionamento();
        dono.setId(1L);
        dono.setEstacionamentos(new ArrayList<>());
        dono.getEstacionamentos().add(estacionamento);
        estacionamento.setDono(dono);
        estacionamentoRepository.save(estacionamento);
        dto = new GerenteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                Date.valueOf("2000-09-12"),
                "12345678900",
                estacionamento.getId()
        );
    }

    @Test
    void deveCadastrarGerenteValido() {
        // Mock the estacionamentoRepository to return the estacionamento
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));

        // Mock save to return the Gerente with ID set
        when(repository.save(any())).thenAnswer(invocation -> {
            Gerente g = invocation.getArgument(0);
            g.setId(1L);
            g.setEstacionamento(estacionamento); // ensure estacionamento is set
            return g;
        });

        GerenteResponse salvo = service.cadastrarGerente(dto);

        assertNotNull(salvo);
        assertEquals("Pedro", salvo.nome());
        assertEquals(1L, salvo.estacionamentoId()); // pega o ID do estacionamento do Gerente salvo
        verify(repository).save(any());
    }


    @Test
    void deveLancarExcecaoAoCadastrarComEstacionamentoInexistente() {
        when(estacionamentoRepository.findById(99L)).thenReturn(Optional.empty());
        GerenteRequest dtoInvalido = new GerenteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                Date.valueOf("2000-09-12"),
                "12345678900",
                99L
        );
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class, () -> service.cadastrarGerente(dtoInvalido));
        assertEquals("Id do estacionamento não encontrado no sistema", ex.getMessage());
    }

    @Test
    void deveBuscarGerentePorId() {
        Gerente entidade = GerenteMapper.toEntity(dto);
        entidade.setId(1L);
        entidade.setEstacionamento(estacionamento);

        when(repository.findById(1L)).thenReturn(Optional.of(entidade));
        GerenteResponse resultado = service.buscarPorId(1L);
        assertEquals("Pedro", resultado.nome());
        verify(repository).findById(1L);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
                () -> service.buscarPorId(99L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveAtualizarGerenteComSucesso() {
        Gerente entidade = GerenteMapper.toEntity(dto);
        entidade.setId(1L);
        entidade.setEstacionamento(estacionamento);

        GerenteRequest dtoAtualizado = new GerenteRequest(
                "Arthur",
                "arthur@gmail.com",
                "123456",
                Date.valueOf("2000-09-12"),
                "12345678900",
                1L
        );

        when(repository.findById(1L)).thenReturn(Optional.of(entidade));
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.of(estacionamento));
        when(repository.save(any())).thenAnswer(invocation -> {
            Gerente g = invocation.getArgument(0);
            g.setId(1L);
            return g;
        });

        GerenteResponse resultado = service.atualizarGerente(dtoAtualizado, 1L);

        assertEquals("Arthur", resultado.nome());
        assertEquals("arthur@gmail.com", resultado.email());
        verify(repository).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarGerenteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
                () -> service.atualizarGerente(dto, 99L));
        assertEquals("Id do gerente buscado não encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComEstacionamentoInexistente() {
        Gerente entidade = GerenteMapper.toEntity(dto);
        entidade.setId(1L);
        entidade.setEstacionamento(estacionamento);

        when(repository.findById(1L)).thenReturn(Optional.of(entidade));
        when(estacionamentoRepository.findById(1L)).thenReturn(Optional.empty());

        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
                () -> service.atualizarGerente(dto, 1L));
        assertEquals("Estacionamento desejado a adicionar não cadastrado no sistema!", ex.getMessage());
    }

    @Test
    void deveDeletarGerenteExistente() {
        Gerente gerente = new Gerente();
        gerente.setId(1L);
        gerente.setNome("Pedro");
        gerente.setEstacionamento(estacionamento);
        estacionamento.getGerentes().add(gerente);

        when(repository.findById(1L)).thenReturn(Optional.of(gerente));
        when(repository.save(any())).thenReturn(gerente);

        service.deletarGerente(1L);

        assertFalse(gerente.getStatus());
        assertFalse(estacionamento.getGerentes().contains(gerente));
        verify(repository).save(gerente);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoDeletarGerenteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
                () -> service.deletarGerente(99L));

        assertEquals("Id do gerente buscado não encontrado no sistema!", ex.getMessage());
    }
}