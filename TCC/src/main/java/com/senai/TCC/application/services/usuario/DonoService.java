package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dto.request.usuario.DonoCreateRequest;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import jakarta.transaction.Transactional;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
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

    public List<DonoResponse> listarDonos() {
        return donoRepository.findAll()
                .stream()
                .map(DonoResponse::fromEntity)
                .toList();
    }

    @Transactional
    public DonoResponse cadastrarDono(DonoCreateRequest dto) {
        DonoEstacionamento dono = dto.toEntity();

        return DonoResponse.fromEntity(donoRepository.save(dono));
    }

    @Transactional
    public DonoResponse atualizarDono(DonoCreateRequest dto, Long id) {
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastrado("O Id não foi encontrado no sistema!");
        }

        DonoEstacionamento dono = optDono.get();

        dono.setNome(dto.nome());
        dono.setEmail(dto.email());
        dono.setSenha(dto.senha());
        dono.setDataNascimento(dto.dataNascimento());

        return DonoResponse.fromEntity(donoRepository.save(optDono.get()));
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
