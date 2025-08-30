package com.senai.TCC.application.services.usuario;

import com.senai.TCC.application.dtos.usuarioDTO.GerenteDTO;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.GerenteRepository;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Gerente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GerenteService {
    @Autowired
    private GerenteRepository gerenteRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public List<GerenteDTO> listarGerentes() {
        return gerenteRepository.findAll()
                .stream()
                .map(GerenteDTO::toDTO)
                .toList();
    }

    public GerenteDTO cadastrarGerente(GerenteDTO dto) {
        Gerente gerente = dto.fromDTO();
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacionamentoId());

        if (optEstacionamento.isPresent()) {
            gerente.setEstacionamento(optEstacionamento.get());
        } else {
            throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
        }

        gerenteRepository.save(gerente);

        return dto;
    }

    public GerenteDTO atualizarGerente(GerenteDTO dto, Long id) {
        var optGerente = gerenteRepository.findById(id);

        if (optGerente.isPresent()) {
            var gerente = optGerente.get();
            gerente.setNome(dto.nome());
            gerente.setEmail(dto.email());
            gerente.setSenha(dto.senha());
            gerente.setDataNascimento(dto.dataNascimento());
            gerenteRepository.save(gerente);
        }

        return dto;
    }

    public void deletarGerente(Long id) {
        gerenteRepository.deleteById(id);
    }
}
