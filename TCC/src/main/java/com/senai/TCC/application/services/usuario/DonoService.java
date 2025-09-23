package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.mappers.usuario.DonoMapper;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import jakarta.transaction.Transactional;
import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import com.senai.TCC.model.exceptions.IdNaoCadastradoException;
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
        return donoRepository.findByStatusTrue()
                .stream()
                .map(DonoMapper::fromEntity)
                .toList();
    }

    public DonoResponse buscarPorId(Long id) {
        Optional<DonoEstacionamento> optionalDono = donoRepository.findById(id);

        if (optionalDono.isEmpty()) {
            throw new IdNaoCadastradoException("ID buscado não foi encontrado no sistema!");
        }

        return DonoMapper.fromEntity(optionalDono.get());
    }

    @Transactional
    public DonoResponse cadastrarDono(DonoRequest dto) {
        DonoEstacionamento dono = DonoMapper.toEntity(dto);
        dono.setStatus(true);
        return DonoMapper.fromEntity(donoRepository.save(dono));
    }

    @Transactional
    public DonoResponse atualizarDono(DonoRequest dto, Long id) {
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastradoException("O Id não foi encontrado no sistema!");
        }

        DonoEstacionamento dono = optDono.get();

        dono.setNome(dto.nome());
        dono.setEmail(dto.email());
        dono.setSenha(dto.senha());
        dono.setDataNascimento(dto.dataNascimento());

        return DonoMapper.fromEntity(donoRepository.save(optDono.get()));
    }

    @Transactional
    public void deletarDono(Long id) {
        Optional<DonoEstacionamento> optDono = donoRepository.findById(id);

        if (optDono.isEmpty()) {
            throw new IdNaoCadastradoException("Dono buscado não cadastrado no sistema");
        }

        DonoEstacionamento dono = optDono.get();

        donoRepository.delete(dono); // <- delete
    }
}
