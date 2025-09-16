package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dto.create_requests.usuario.ClienteCreateRequest;
import com.senai.TCC.application.mappers.usuario.ClienteMapper;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findByStatusTrue()
                .stream()
                .map(ClienteMapper::fromEntity)
                .toList();
    }

    public ClienteResponse cadastrarCliente(ClienteCreateRequest dto) {
        Cliente cliente = ClienteMapper.toEntity(dto);
        cliente.setStatus(true);

        return ClienteMapper.fromEntity(
                clienteRepository.save(cliente));
    }

    public ClienteResponse atualizarCliente(ClienteCreateRequest dto, Long id) {
        Optional<Cliente> optCliente = clienteRepository.findById(id);

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente buscado não cadastrado no sistema");
        }

        Cliente cliente = optCliente.get();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setSenha(dto.senha());
        cliente.setDataNascimento(dto.dataNascimento());

        return ClienteMapper.fromEntity(clienteRepository.save(cliente));
    }

    public void deletarCliente(Long id) {
        Optional<Cliente> optCliente = clienteRepository.findById(id);

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente buscado não cadastrado no sistema");
        }

        Cliente cliente = optCliente.get();
        cliente.setStatus(false);
        clienteRepository.save(cliente);
    }
}
