package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dtos.usuarioDTO.DonoDTO;
import jakarta.transaction.Transactional;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.exceptions.IdDeDonoNaoCadastrado;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonoService {
    @Autowired
    private DonoRepository donoRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public List<DonoDTO> listarDonos() {
        return donoRepository.findAll()
                .stream()
                .map(DonoDTO::toDTO)
                .toList();
    }

    @Transactional
    public DonoDTO cadastrarDono(DonoDTO dto) {
        DonoEstacionamento dono = dto.fromDTO();

        donoRepository.save(dono);

        return dto;
    }

    @Transactional
    public DonoDTO atualizarDono(DonoDTO dto, Long id) {
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdDeDonoNaoCadastrado("O Id não foi encontrado no sistema!");
        } else {
            optDono.get().setNome(dto.nome());
            optDono.get().setEmail(dto.email());
            optDono.get().setSenha(dto.senha());
            optDono.get().setDataNascimento(dto.dataNascimento());
            donoRepository.save(optDono.get());
        }

        return dto;
    }

    @Transactional
    public void deletarDono(Long id) {
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdDeDonoNaoCadastrado("O Id não foi encontrado no sistema!");
        } else {
            donoRepository.delete(optDono.get());
        }
    }
}
