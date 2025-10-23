package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.mappers.EstacionamentoMapper;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import jakarta.transaction.Transactional;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstacionamentoService {
    private final EstacionamentoRepository estacionamentoRepository;

    private final DonoRepository donoRepository;

    public EstacionamentoService(EstacionamentoRepository estacionamentoRepository, DonoRepository donoRepository) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.donoRepository = donoRepository;
    }

    public List<EstacionamentoResponse> listarTodosOsEstacionamentos() {
        return estacionamentoRepository.findByStatusTrue()
                .stream()
                .map(EstacionamentoMapper::fromEntity)
                .toList();
    }

    public EstacionamentoResponse buscarPorId(Long id) {
        Optional<Estacionamento> optionalEstacionamento = estacionamentoRepository.findById(id);

        if (optionalEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return EstacionamentoMapper.fromEntity(optionalEstacionamento.get());
    }

    @Transactional
    public EstacionamentoResponse cadastrarEstacionamento(EstacionamentoRequest dto, Long id) {
        Estacionamento novoEst = EstacionamentoMapper.toEntity(dto);
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastrado("O Id do dono fornecido não foi encontrado no sistema!");
        } else {
            novoEst.setDonoEstacionamento(optDono.get());
            optDono.get().getEstacionamentos().add(novoEst);
            novoEst.setFuncionamento(true);
        }

        novoEst.setStatus(true);
        return EstacionamentoMapper.fromEntity(estacionamentoRepository.save(novoEst));
    }

    @Transactional
    public EstacionamentoResponse atualizarEstacionamento(EstacionamentoRequest dto, Long id) {
        Optional<Estacionamento> optEst = estacionamentoRepository.findById(id);

        if (optEst.isEmpty()) {
            throw new IdNaoCadastrado("O Id do estacionamento fornecido não foi encontrado no sistema!");
        } else {
            optEst.get().setFoto(dto.foto());
            optEst.get().setHoraAbertura(dto.horaAbertura());
            optEst.get().setMaxVagas(dto.maximoDeVagas());
            optEst.get().setHoraFechamento(dto.horaFechamento());
            optEst.get().setNome(dto.nome());
        }

        return EstacionamentoMapper.fromEntity(estacionamentoRepository.save(optEst.get()));
    }

    @Transactional
    public void desativarEstacionamento(Long id) {
        Optional<Estacionamento> optEst = estacionamentoRepository.findById(id);

        if (optEst.isEmpty()) {
            throw new IdNaoCadastrado("O Id do estacionamento fornecido não foi encontrado no sistema!");
        }

        Estacionamento estacionamento = optEst.get();
        estacionamento.getDonoEstacionamento().getEstacionamentos().remove(estacionamento);

        estacionamento.setStatus(false);
        estacionamentoRepository.save(estacionamento);
    }
}
