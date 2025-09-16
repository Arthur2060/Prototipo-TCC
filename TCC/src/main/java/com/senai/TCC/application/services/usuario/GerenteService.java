package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dto.create_requests.usuario.GerenteCreateRequest;
import com.senai.TCC.application.mappers.usuario.GerenteMapper;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.GerenteRepository;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GerenteService {
    private final GerenteRepository gerenteRepository;

    private final EstacionamentoRepository estacionamentoRepository;

    public GerenteService(GerenteRepository gerenteRepository, EstacionamentoRepository estacionamentoRepository) {
        this.gerenteRepository = gerenteRepository;
        this.estacionamentoRepository = estacionamentoRepository;
    }

    public List<GerenteResponse> listarGerentes() {
        return gerenteRepository.findByStatusTrue()
                .stream()
                .map(GerenteMapper::fromEntity)
                .toList();
    }

    public GerenteResponse cadastrarGerente(GerenteCreateRequest dto) {
        Gerente gerente = GerenteMapper.toEntity(dto);
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacionamentoId());

        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
        }

        Estacionamento estacionamento = optEstacionamento.get();

        estacionamento.getGerentes().add(gerente);
        gerente.setEstacionamento(estacionamento);
        gerente.setStatus(true);

        return GerenteMapper.fromEntity(gerenteRepository.save(gerente));
    }

    public GerenteResponse atualizarGerente(GerenteCreateRequest dto, Long id) {
        var optGerente = gerenteRepository.findById(id);

        if (optGerente.isEmpty()) {
            throw new IdNaoCadastrado("Id do gerente buscado não encontrado no sistema!");
        }

        Gerente gerente = optGerente.get();

        gerente.setNome(dto.nome());
        gerente.setEmail(dto.email());
        gerente.setSenha(dto.senha());
        gerente.setDataNascimento(dto.dataNascimento());

        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(gerente.getEstacionamento().getId());

        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Estacionamento desejado a adicionar não cadastrado no sistema!");
        }

        Estacionamento estacionamento = optEstacionamento.get();

        gerente.setEstacionamento(estacionamento);
        estacionamento.getGerentes().add(gerente);

        return GerenteMapper.fromEntity(gerenteRepository.save(gerente));
    }

    public void deletarGerente(Long id) {
        Optional<Gerente> optGerente = gerenteRepository.findById(id);

        if (optGerente.isEmpty()) {
            throw new IdNaoCadastrado("Id do gerente buscado não encontrado no sistema!");
        }

        Gerente gerente = optGerente.get();
        gerente.getEstacionamento().getGerentes().remove(gerente);
        gerente.setStatus(false);
        gerenteRepository.save(gerente);
    }
}
