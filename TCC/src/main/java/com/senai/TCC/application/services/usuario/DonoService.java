package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dtos.usuarioDTO.DonoDTO;
import jakarta.transaction.Transactional;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.DonoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DonoService {
    private final DonoRepository donoRepository;

    public DonoService(DonoRepository donoRepository) {
        this.donoRepository = donoRepository;
    }

    public List<DonoDTO> listarDonos() {
        return donoRepository.findAll()
                .stream()
                .map(DonoDTO::fromEntity)
                .toList();
    }

    @Transactional
    public DonoDTO cadastrarDono(DonoDTO dto) {
        DonoEstacionamento dono = dto.toEntity();

        return DonoDTO.fromEntity(donoRepository.save(dono));
    }

    @Transactional
    public DonoDTO atualizarDono(DonoDTO dto, Long id) {
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastrado("O Id não foi encontrado no sistema!");
        }

        DonoEstacionamento dono = optDono.get();

        dono.setNome(dto.nome());
        dono.setEmail(dto.email());
        dono.setSenha(dto.senha());
        dono.setDataNascimento(dto.dataNascimento());

        return DonoDTO.fromEntity(donoRepository.save(optDono.get()));
    }

    @Transactional
    public void deletarDono(Long id) {
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastrado("O Id não foi encontrado no sistema!");
        }

        DonoEstacionamento dono = optDono.get();

        donoRepository.delete(dono);
    }
}
