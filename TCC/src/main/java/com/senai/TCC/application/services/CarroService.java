package com.senai.TCC.application.services;

import com.senai.TCC.application.dtos.CarroDTO;
import com.senai.TCC.infraestructure.repositories.CarroRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Carro;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarroService {
    @Autowired
    private CarroRepository carroRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<CarroDTO> listarCarros() {
        return carroRepository.findAll()
                .stream()
                .map(CarroDTO::toDTO)
                .toList();
    }

    @Transactional
    public CarroDTO cadastrarCarro(CarroDTO dto) {
        Carro novoCarro = dto.fromDTO();
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente não encontrado no sistema!");
        }

        Cliente cliente = optCliente.get();

        novoCarro.setCliente(cliente);
        cliente.getCarros().add(novoCarro);

        clienteRepository.save(cliente);
        return CarroDTO.toDTO(carroRepository.save(novoCarro));
    }

    @Transactional
    public CarroDTO atualizarCarro(CarroDTO dto, Long id) {
        Optional<Carro> optionalCarro = carroRepository.findById(id);
        Optional<Cliente> optionalCliente = clienteRepository.findById(dto.clienteId());

        if (optionalCarro.isEmpty() || optionalCliente.isEmpty()) {
            throw new IdNaoCadastrado("Carro ou cliente necessitado não encontrado no sistema?");
        }


        Cliente novoCliente = optionalCliente.get();
        Carro carro = optionalCarro.get();
        Cliente clienteOriginal = carro.getCliente();

        if (clienteOriginal != novoCliente){
            clienteOriginal.getCarros().remove(carro);
            carro.setCliente(novoCliente);
            novoCliente.getCarros().add(carro);
            clienteRepository.save(novoCliente);
            clienteRepository.save(clienteOriginal);
        }

        carro.setPlaca(dto.placa());
        carro.setCor(dto.cor());
        carro.setModelo(dto.modelo());

        return CarroDTO.toDTO(carroRepository.save(carro));
    }

    @Transactional
    public void deletarCarro(Long id) {
        Optional<Carro> optionalCarro = carroRepository.findById(id);

        if (optionalCarro.isEmpty()) {
            throw new IdNaoCadastrado("Carro não encontrado no sistema!");
        }

        Carro carro = optionalCarro.get();

        carro.getCliente().getCarros().remove(carro);
        carroRepository.delete(carro);
    }
}
