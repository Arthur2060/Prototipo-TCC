package application.services;

import application.dtos.EstacionamentoDTO;
import jakarta.transaction.Transactional;
import model.entities.Estacionamento;
import model.entities.usuarios.DonoEstacionamento;
import model.exceptions.IdDeDonoNaoCadastrado;
import model.exceptions.IdDeEstacionamentoNaoEncontrado;
import model.repositories.EstacionamentoRepository;
import model.repositories.usuario.DonoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstacionamentoService {
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private DonoRepository donoRepository;

    public List<EstacionamentoDTO> listarTodosOsEstacionamentos() {
        return estacionamentoRepository.findAll()
                .stream()
                .map(EstacionamentoDTO::toDTO)
                .toList();
    }

    @Transactional
    public EstacionamentoDTO cadastrarEstacionamento(EstacionamentoDTO dto, Long id) {
        Estacionamento novoEst = dto.fromDTO();
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdDeDonoNaoCadastrado("O Id do dono fornecido não foi encontrado no sistema!");
        } else {
            novoEst.setDono(optDono.get());
            novoEst.setStatus(true);
        }

        return EstacionamentoDTO.toDTO(novoEst);
    }

    @Transactional
    public EstacionamentoDTO atualizarEstacionamento(EstacionamentoDTO dto, Long id) {
        Optional<Estacionamento> optEst = estacionamentoRepository.findById(id);

        if (optEst.isEmpty()) {
            throw new IdDeEstacionamentoNaoEncontrado("O Id do estacionamento fornecido não foi encontrado no sistema!");
        } else {
            optEst.get().setFoto(dto.foto());
            optEst.get().setHoraAbertura(dto.horaAbertura());
            optEst.get().setMaxVagas(dto.maximoDeVagas());
            optEst.get().setHoraFechamento(dto.horaFechamento());
            optEst.get().setNome(dto.nome());
        }

        return EstacionamentoDTO.toDTO(optEst.get());
    }

    @Transactional
    public void desativarEstacionamento(Long id) {
        Optional<Estacionamento> optEst = estacionamentoRepository.findById(id);

        if (optEst.isEmpty()) {
            throw new IdDeEstacionamentoNaoEncontrado("O Id do estacionamento fornecido não foi encontrado no sistema!");
        } else {
            optEst.get().setStatus(false);
        }
    }
}
