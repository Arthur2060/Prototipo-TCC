package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.mappers.usuario.ClienteMapper;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findByStatusTrue()
                .stream()
                .map(ClienteMapper::fromEntity)
                .toList();
    }

    public ClienteResponse buscarPorId(Long id) {
        Optional<Cliente> optionalCliente = clienteRepository.findById(id);

        if (optionalCliente.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return ClienteMapper.fromEntity(optionalCliente.get());
    }

    public ClienteResponse cadastrarCliente(ClienteRequest dto) {
        Cliente cliente = ClienteMapper.toEntity(dto);
        cliente.setSenha(passwordEncoder.encode(dto.senha()));
        cliente.setStatus(true);
        clienteRepository.save(cliente);

        return ClienteMapper.fromEntity(ClienteMapper.toEntity(dto));
    }

    public ClienteResponse atualizarCliente(ClienteRequest dto, Long id) {
        Optional<Cliente> optCliente = clienteRepository.findById(id);

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente buscado não cadastrado no sistema");
        }

        Cliente cliente = optCliente.get();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setSenha(dto.senha());
        cliente.setDataNascimento(dto.dataNascimento());

        return ClienteMapper.fromEntity(cliente);
    }

    public void deletarCliente(Long id) {
        Optional<Cliente> optCliente = clienteRepository.findById(id);

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente buscado não cadastrado no sistema");
        }

        Cliente cliente = optCliente.get();

        clienteRepository.delete(cliente);
    }

}
