package com.senai.TCC.application.services;

import com.senai.TCC.application.dtos.EstacionamentoDTO;
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

    public List<EstacionamentoDTO> listarTodosOsEstacionamentos() {
        return estacionamentoRepository.findAll()
                .stream()
                .map(EstacionamentoDTO::fromEntity)
                .toList();
    }

    @Transactional
    public EstacionamentoDTO cadastrarEstacionamento(EstacionamentoDTO dto, Long id) {
        Estacionamento novoEst = dto.toEntity();
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastrado("O Id do dono fornecido não foi encontrado no sistema!");
        } else {
            novoEst.setDono(optDono.get());
            optDono.get().getEstacionamentos().add(novoEst);
            novoEst.setFuncionamento(true);
        }

        return EstacionamentoDTO.fromEntity(estacionamentoRepository.save(novoEst));
    }

    @Transactional
    public EstacionamentoDTO atualizarEstacionamento(EstacionamentoDTO dto, Long id) {
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

        return EstacionamentoDTO.fromEntity(estacionamentoRepository.save(optEst.get()));
    }

    @Transactional
    public void desativarEstacionamento(Long id) {
        Optional<Estacionamento> optEst = estacionamentoRepository.findById(id);

        if (optEst.isEmpty()) {
            throw new IdNaoCadastrado("O Id do estacionamento fornecido não foi encontrado no sistema!");
        } else {
            estacionamentoRepository.delete(optEst.get());
        }
    }
}
