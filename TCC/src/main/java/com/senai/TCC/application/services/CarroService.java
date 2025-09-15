package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.request.CarroCreateRequest;
import com.senai.TCC.application.dto.response.CarroResponse;
import com.senai.TCC.infraestructure.repositories.CarroRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Carro;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarroService {
    private final CarroRepository carroRepository;

    private final ClienteRepository clienteRepository;

    public CarroService(CarroRepository carroRepository, ClienteRepository clienteRepository) {
        this.carroRepository = carroRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<CarroResponse> listarCarros() {
        return carroRepository.findAll()
                .stream()
                .map(CarroResponse::fromEntity)
                .toList();
    }

    @Transactional
    public CarroResponse cadastrarCarro(CarroCreateRequest dto) {
        Carro carro = dto.toEntity();
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastrado("Cliente não encontrado com no sistema.");
        }

        Cliente cliente = optCliente.get();
        cliente.getCarros().add(carro);
        carro.setCliente(cliente);

        return CarroResponse.fromEntity(carroRepository.save(carro));
    }

    @Transactional
    public CarroResponse atualizarCarro(CarroCreateRequest dto, Long id) {
        Optional<Carro> carroOriginal = carroRepository.findById(id);
        Optional<Cliente> novoCliente = clienteRepository.findById(dto.clienteId());

        if (carroOriginal.isEmpty() || novoCliente.isEmpty()) {
            throw new IdNaoCadastrado("Carro ou Cliente não encontrado no sistema.");
        }

        Carro carro = carroOriginal.get();
        Cliente clienteOriginal = carro.getCliente();
        Cliente cliente = novoCliente.get();

        if (carro.getCliente() != cliente) {
            clienteOriginal.getCarros().remove(carro);
            carro.setCliente(cliente);
            cliente.getCarros().add(carro);
        }

        carro.setPlaca(dto.placa());
        carro.setModelo(dto.modelo());
        carro.setCor(dto.cor());


        return CarroResponse.fromEntity(carroRepository.save(carro));
    }

    @Transactional
    public void deletarCarro(Long id) {
        Optional<Carro> optCarro = carroRepository.findById(id);

        if (optCarro.isEmpty()) {
            throw new IdNaoCadastrado("Carro não encontrado no sistema.");
        }

        Carro carro = optCarro.get();
        Cliente cliente = carro.getCliente();
        cliente.getCarros().remove(carro);

        carroRepository.delete(carro);
    }
}
