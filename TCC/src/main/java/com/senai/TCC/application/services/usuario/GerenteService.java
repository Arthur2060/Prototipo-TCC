package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dto.requests.usuario.GerenteRequest;
import com.senai.TCC.application.mappers.usuario.GerenteMapper;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.GerenteRepository;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GerenteService {
    private final GerenteRepository gerenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EstacionamentoRepository estacionamentoRepository;

    public List<GerenteResponse> listarGerentes() {
        return gerenteRepository.findByStatusTrue()
                .stream()
                .map(GerenteMapper::fromEntity)
                .toList();
    }

    public GerenteResponse buscarPorId(Long id) {
        Optional<Gerente> optionalGerente = gerenteRepository.findById(id);

        if (optionalGerente.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return GerenteMapper.fromEntity(optionalGerente.get());
    }

    public GerenteResponse cadastrarGerente(GerenteRequest dto) {
        Gerente gerente = GerenteMapper.toEntity(dto);
        gerente.setSenha(passwordEncoder.encode(dto.senha()));
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacionamentoId());

        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
        }

        Estacionamento estacionamento = optEstacionamento.get();

        estacionamento.getGerentes().add(gerente);
        gerente.setEstacionamento(estacionamento);
        gerente.setStatus(true);
        gerenteRepository.save(gerente);

        return GerenteMapper.fromEntity(GerenteMapper.toEntity(dto));
    }

    public GerenteResponse atualizarGerente(GerenteRequest dto, Long id) {
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
