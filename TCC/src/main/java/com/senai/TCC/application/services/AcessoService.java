package com.senai.TCC.application.services;

import com.senai.TCC.application.dtos.AcessoDTO;
import com.senai.TCC.infraestructure.repositories.AcessoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.model.entities.Acesso;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AcessoService {
    @Autowired
    private AcessoRepository acessoRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public List<AcessoDTO> listarAcessos() {
        return acessoRepository.findAll()
                .stream()
                .map(AcessoDTO::toDTO)
                .toList();
    }

    @Transactional
    public AcessoDTO cadastrarAcesso(AcessoDTO dto) {
        Acesso acesso = dto.fromDTO();

        acesso.calcularHorasTotais();
        if (estacionamentoRepository.findById(dto.estacioId()).isPresent()) {
            acesso.setEstacionamento(estacionamentoRepository.findById(dto.estacioId()).get());
        } else {
            throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
        }

        return AcessoDTO.toDTO(acessoRepository.save(acesso));
    }

    @Transactional
    public AcessoDTO atualizarAcesso(AcessoDTO dto, Long id) {
        Optional<Acesso> optAcesso = acessoRepository.findById(id);

        if (optAcesso.isEmpty()) {
            throw new IdNaoCadastrado("O Acesso buscado não existe no sistema");
        } else {
            optAcesso.get().setHoraDeEntrada(dto.horaDeEntrada());
            optAcesso.get().setHoraDeSaida(dto.horaDeSaida());
            optAcesso.get().calcularHorasTotais();
            optAcesso.get().setPlacaDoCarro(dto.placaDoCarro());
            optAcesso.get().setValorAPagar(dto.valorAPagar());
            if (estacionamentoRepository.findById(dto.estacioId()).isPresent()) {
                optAcesso.get().setEstacionamento(estacionamentoRepository.findById(dto.estacioId()).get());
            } else {
                throw new IdNaoCadastrado("Id do estacionamento não encontrado no sistema");
            }
            return AcessoDTO.toDTO(acessoRepository.save(optAcesso.get()));
        }
    }

    @Transactional
    public void deletarAcesso(Long id) {
        if (acessoRepository.findById(id).isPresent()) {
            acessoRepository.delete(acessoRepository.findById(id).get());
        }
    }
}
