package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dtos.usuarioDTO.ClienteDTO;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteDTO> listarClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(ClienteDTO::toDTO)
                .toList();
    }

    public ClienteDTO cadastrarCliente(ClienteDTO dto) {
        Cliente cliente = dto.fromDTO();

        clienteRepository.save(cliente);

        return dto;
    }

    public ClienteDTO atualizarCliente(ClienteDTO dto, Long id) {
        var optCliente = clienteRepository.findById(id);

        if (optCliente.isPresent()) {
            var cliente = optCliente.get();
            cliente.setNome(dto.nome());
            cliente.setEmail(dto.email());
            cliente.setSenha(dto.senha());
            cliente.setDataNascimento(dto.dataNascimento());
            cliente.setPlacaDoCarro(dto.placaDoCarro());
            clienteRepository.save(cliente);
        }

        return dto;
    }

    public void deletarCliente(Long id) {
        clienteRepository.deleteById(id);
    }
}
