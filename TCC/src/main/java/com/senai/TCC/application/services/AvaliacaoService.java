package com.senai.TCC.application.services;

import com.senai.TCC.application.dtos.AvaliacaoDTO;
import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<AvaliacaoDTO> listarAvaliacoes() {
        return avaliacaoRepository.findAll()
                .stream()
                .map(AvaliacaoDTO::toDTO)
                .toList();
    }

    @Transactional
    public AvaliacaoDTO cadastrarAvaliacao(AvaliacaoDTO dto) {
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(dto.estacioId());

        if (optCliente.isEmpty() || optEstacio.isEmpty()) {
            throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
        } else {
            Avaliacao avaliacao = dto.fromDTO();
            avaliacao.setCliente(optCliente.get());
            avaliacao.setEstacionamento(optEstacio.get());
            optEstacio.get().getAvaliacoes().add(avaliacao);

            return AvaliacaoDTO.toDTO(avaliacaoRepository.save(avaliacao));
        }
    }

    @Transactional
    public AvaliacaoDTO atualizarAvaliacao(AvaliacaoDTO dto, Long id) {
        Optional<Avaliacao> optAvaliacao = avaliacaoRepository.findById(id);
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(dto.estacioId());
        if (optAvaliacao.isEmpty()) {
            throw new IdNaoCadastrado("A avaliação buscada não existe no sistema");
        } else if (optCliente.isEmpty() || optEstacio.isEmpty()) {
                throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
            } else {
                optAvaliacao.get().setNota(dto.nota());
                optAvaliacao.get().setComentario(dto.comentario());
                optAvaliacao.get().setDataDeAvaliacao(dto.dataDeAvaliacao());
                optAvaliacao.get().setCliente(optCliente.get());
                optAvaliacao.get().setEstacionamento(optEstacio.get());

                return AvaliacaoDTO.toDTO(avaliacaoRepository.save(optAvaliacao.get()));
            }
    }

    @Transactional
    public void deletarAvaliacao(Long id) {
        if (avaliacaoRepository.findById(id).isPresent()) {
            avaliacaoRepository.delete(avaliacaoRepository.findById(id).get());
        }
    }
}
