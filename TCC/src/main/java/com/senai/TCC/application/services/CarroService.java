package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.requests.CarroRequest;
import com.senai.TCC.application.mappers.CarroMapper;
import com.senai.TCC.application.dto.response.CarroResponse;
import com.senai.TCC.infraestructure.repositories.CarroRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Carro;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastradoException;
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
        return carroRepository.findByStatusTrue()
                .stream()
                .map(CarroMapper::fromEntity)
                .toList();
    }

    public CarroResponse buscarPorId(Long id) {
        Optional<Carro> optionalCarro = carroRepository.findById(id);

        if (optionalCarro.isEmpty()) {
            throw new IdNaoCadastradoException("ID buscado não foi encontrado no sistema!");
        }

        return CarroMapper.fromEntity(optionalCarro.get());
    }

    @Transactional
    public CarroResponse cadastrarCarro(CarroRequest dto) {
        Carro carro = CarroMapper.toEntity(dto);
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());

        if (optCliente.isEmpty()) {
            throw new IdNaoCadastradoException("Cliente não encontrado com no sistema.");
        }

        Cliente cliente = optCliente.get();
        cliente.getCarros().add(carro);
        carro.setCliente(cliente);

        carro.setStatus(true);
        return CarroMapper.fromEntity(carroRepository.save(carro));
    }

    @Transactional
    public CarroResponse atualizarCarro(CarroRequest dto, Long id) {
        Optional<Carro> carroOriginal = carroRepository.findById(id);
        Optional<Cliente> novoCliente = clienteRepository.findById(dto.clienteId());

        if (carroOriginal.isEmpty() || novoCliente.isEmpty()) {
            throw new IdNaoCadastradoException("Carro ou Cliente não encontrado no sistema.");
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


        return CarroMapper.fromEntity(carroRepository.save(carro));
    }

    @Transactional
    public void deletarCarro(Long id) {
        Optional<Carro> optCarro = carroRepository.findById(id);

        if (optCarro.isEmpty()) {
            throw new IdNaoCadastradoException("Carro não encontrado no sistema.");
        }

        Carro carro = optCarro.get();
        Cliente cliente = carro.getCliente();
        cliente.getCarros().remove(carro);

        carro.setStatus(false);
        carroRepository.save(carro);
    }
}
