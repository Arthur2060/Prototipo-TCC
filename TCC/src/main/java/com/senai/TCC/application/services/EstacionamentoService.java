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

import java.util.ArrayList;
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
        Estacionamento estacionamento = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("ID buscado não foi encontrado no sistema!"));

        return EstacionamentoMapper.fromEntity(estacionamento);
    }

    @Transactional
    public EstacionamentoResponse cadastrarEstacionamento(EstacionamentoRequest dto, Long id) {
        DonoEstacionamento dono = donoRepository.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("O Id do dono fornecido não foi encontrado no sistema!"));

        Estacionamento novoEst = EstacionamentoMapper.toEntity(dto);

        if (dono.getEstacionamentos() == null) {
            dono.setEstacionamentos(new ArrayList<>());
        }

        novoEst.setDono(dono);
        dono.getEstacionamentos().add(novoEst);
        novoEst.setFuncionamento(true);
        novoEst.setStatus(true);

        return EstacionamentoMapper.fromEntity(estacionamentoRepository.save(novoEst));
    }

    @Transactional
    public EstacionamentoResponse atualizarEstacionamento(EstacionamentoRequest dto, Long id) {
        Estacionamento estacionamento = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("O Id do estacionamento fornecido não foi encontrado no sistema!"));

        estacionamento.setFoto(dto.foto());
        estacionamento.setHoraAbertura(dto.horaAbertura());
        estacionamento.setMaxVagas(dto.maximoDeVagas());
        estacionamento.setHoraFechamento(dto.horaFechamento());
        estacionamento.setNome(dto.nome());

        return EstacionamentoMapper.fromEntity(estacionamentoRepository.save(estacionamento));
    }

    @Transactional
    public void desativarEstacionamento(Long id) {
        Estacionamento estacionamento = estacionamentoRepository.findById(id)
                .orElseThrow(() -> new IdNaoCadastrado("O Id do estacionamento fornecido não foi encontrado no sistema!"));

        // **CORREÇÃO: Verifica se a lista de estacionamentos do dono é nula e a inicializa**
        if (estacionamento.getDono().getEstacionamentos() == null) {
            estacionamento.getDono().setEstacionamentos(new ArrayList<>());
        }

        estacionamento.getDono().getEstacionamentos().remove(estacionamento);
        estacionamento.setStatus(false);
        estacionamentoRepository.save(estacionamento);
    }
}