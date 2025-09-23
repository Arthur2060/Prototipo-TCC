package application.service.usuarios;

import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.application.mappers.usuario.GerenteMapper;
import com.senai.TCC.application.services.usuario.GerenteService;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.GerenteRepository;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.exceptions.IdNaoCadastradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GerenteServiceTest {

    @Mock
    private GerenteRepository repository;
    @Mock
    private EstacionamentoRepository estacionamentoRepository;
    @InjectMocks
    private GerenteService service;

    @Test
    void deveCadastrarGerenteValido() {
        GerenteRequest dto = new GerenteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12"),
                "12345678900",
                1L
        );

        Gerente entidade = GerenteMapper.toEntity(dto);

        when(repository.save(any())).thenReturn(entidade);

        GerenteResponse salvo = service.cadastrarGerente(dto);

        assertNotNull(salvo);
        assertEquals("Pedro", salvo.nome());
        verify(repository).save(any());
    }

//    @Test
//    void deveBuscarClientePorId() {
//        ClienteCreateRequest dto = new ClienteCreateRequest(
//                "Pedro",
//                "pedro@gmail.com",
//                "123456",
//                java.sql.Date.valueOf("2000-09-12")
//        );
//
//        Cliente entidade = ClienteMapper.toEntity(dto);
//        when(repository.findById(1L)).thenReturn(Optional.of(entidade));
//        Cliente resultado = service.buscarPorId(1L);
//        assertEquals("Pedro", resultado.getNome());
//        verify(repository).findById(1L);
//    }
//
//    @Test
//    void deveLancarIdDesconhecidoExceptionAoBuscarIdInexistente() {
//        when(repository.findById(99L)).thenReturn(Optional.empty());
//
//        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
//                () -> service.buscarPorId(99L));
//        assertEquals("Gerente com ID 99 não encontrado.", ex.getMessage());
//    }

    @Test
    void deveAtualizarGerenteComSucesso() {
        GerenteRequest existente = new GerenteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12"),
                "12345678900",
                1L
        );

        GerenteRequest dtoAtualizado = new GerenteRequest(
                "Arthur",
                "arthur@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12"),
                "12345678900",
                1L
        );

        Gerente salvo = GerenteMapper.toEntity(dtoAtualizado);
        salvo.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(GerenteMapper.toEntity(existente)));
        when(repository.save(any())).thenReturn(salvo);

        GerenteResponse resultado = service.atualizarGerente(dtoAtualizado, 1L);

        assertEquals("Arthur", resultado.nome());
        assertEquals("arthur@gmail.com", resultado.email());
        verify(repository).save(any());
    }

    @Test
    void deveDeletarGerenteExistente() {
        Gerente gerente = new Gerente();
        gerente.setId(1L);
        gerente.setNome("Pedro");

        when(repository.findById(1L)).thenReturn(Optional.of(gerente));

        service.deletarGerente(1L);

        verify(repository).delete(gerente);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoDeletarGerenteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IdNaoCadastradoException ex = assertThrows(IdNaoCadastradoException.class,
                () -> service.deletarGerente(99L));

        assertEquals("Id do gerente buscado não encontrado no sistema!", ex.getMessage());
    }
}
