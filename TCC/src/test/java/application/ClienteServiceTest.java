package application;

import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.application.mappers.usuario.ClienteMapper;
import com.senai.TCC.application.services.usuario.ClienteService;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.usuarios.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void deveCadastrarClienteValido() {
        ClienteRequest dto = new ClienteRequest(
                "Nome",
                "email",
                "sadadwa",
                new Date()
        );

        Cliente entidade = ClienteMapper.toEntity(dto);

        when(clienteRepository.save(any())).thenReturn(entidade);

        ClienteResponse salvo = service.cadastrarCliente(dto);

        assertNotNull(salvo);
    }
}
