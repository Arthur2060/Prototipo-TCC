package application.service.usuarios;

import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.application.mappers.usuario.ClienteMapper;
import com.senai.TCC.application.services.usuario.ClienteService;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;
    @InjectMocks
    private ClienteService service;

    @Test
    void deveCadastrarClienteValido() {
        ClienteRequest dto = new ClienteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        Cliente entidade = ClienteMapper.toEntity(dto);

        when(repository.save(any())).thenReturn(entidade);

        ClienteResponse salvo = service.cadastrarCliente(dto);

        assertNotNull(salvo);
        assertEquals("Pedro", salvo.nome());
        verify(repository).save(any());
    }

    @Test
    void deveBuscarClientePorId() {
        ClienteRequest dto = new ClienteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        Cliente entidade = ClienteMapper.toEntity(dto);
        when(repository.findById(1L)).thenReturn(Optional.of(entidade));
        ClienteResponse resultado = service.buscarPorId(1L);
        assertEquals("Pedro", resultado.nome());
        verify(repository).findById(1L);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoBuscarIdInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IdNaoCadastradoException ex = assertThrows(IdNaoCadastradoException.class,
                () -> service.buscarPorId(99L));
        assertEquals("ID buscado não foi encontrado no sistema!", ex.getMessage());
    }

    @Test
    void deveAtualizarClienteComSucesso() {
        ClienteRequest existente = new ClienteRequest(
                "Pedro",
                "pedro@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );

        ClienteRequest dtoAtualizado = new ClienteRequest(
                "Arthur",
                "arthur@gmail.com",
                "123456",
                java.sql.Date.valueOf("2000-09-12")
        );
        Cliente salvo = ClienteMapper.toEntity(dtoAtualizado);
        salvo.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(ClienteMapper.toEntity(existente)));
        when(repository.save(any())).thenReturn(salvo);

        ClienteResponse resultado = service.atualizarCliente(dtoAtualizado, 1L);

        assertEquals("Arthur", resultado.nome());
        assertEquals("arthur@gmail.com", resultado.email());
        verify(repository).save(any());
    }

    @Test
    void deveDeletarClienteExistente() {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Pedro");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        service.deletarCliente(1L);

        verify(repository).delete(cliente);
    }

    @Test
    void deveLancarIdDesconhecidoExceptionAoDeletarClienteInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        IdNaoCadastradoException ex = assertThrows(IdNaoCadastradoException.class,
                () -> service.deletarCliente(99L));

        assertEquals("Cliente buscado não cadastrado no sistema", ex.getMessage());
    }
}
