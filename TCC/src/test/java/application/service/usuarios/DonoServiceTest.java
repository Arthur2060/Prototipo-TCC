package application.service.usuarios;

import com.senai.TCC.application.dto.create_requests.usuario.DonoCreateRequest;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.application.mappers.usuario.DonoMapper;
import com.senai.TCC.application.services.usuario.DonoService;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
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
public class DonoServiceTest {

    @Mock
    private DonoRepository repository;
    @InjectMocks
    private DonoService service;

    @Test
    void deveCadastrarDonoValido() {
        DonoCreateRequest dto = new DonoCreateRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoEstacionamento entidade = DonoMapper.toEntity(dto);

        when(repository.save(any())).thenReturn(entidade);

        DonoResponse salvo = service.cadastrarDono(dto);

        assertNotNull(salvo);
        assertEquals("Pedro", salvo.nome());
        verify(repository).save(any());
    }

//    @Test
//    void deveBuscarDonoPorId() {
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
//        assertEquals("Dono com ID 99 não encontrado.", ex.getMessage());
//    }

    @Test
    void deveAtualizarDonoComSucesso() {
        DonoCreateRequest existente = new DonoCreateRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoCreateRequest dtoAtualizado = new DonoCreateRequest(
                "Arthur",
                "arthur@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        DonoEstacionamento salvo = DonoMapper.toEntity(dtoAtualizado);
        salvo.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(DonoMapper.toEntity(existente)));
        when(repository.save(any())).thenReturn(salvo);

        DonoResponse resultado = service.atualizarDono(dtoAtualizado, 1L);

        assertEquals("Arthur", resultado.nome());
        assertEquals("arthur@gmail.com", resultado.email());
        verify(repository).save(any());
    }

    @Test
    void deveDeletarDonoExistente() {
        DonoEstacionamento dono = new DonoEstacionamento();
        dono.setId(1L);
        dono.setNome("Pedro");

        when(repository.findById(1L)).thenReturn(Optional.of(dono));

        service.deletarDono(1L);

        verify(repository).delete(dono);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoDeletarDonoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IdNaoCadastrado ex = assertThrows(IdNaoCadastrado.class,
                () -> service.deletarDono(99L));

        assertEquals("O Id não foi encontrado no sistema!", ex.getMessage());
    }
}
