package com.senai.TCC.application.services;

import com.senai.TCC.application.dtos.AvaliacaoDTO;
import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import com.senai.TCC.model.exceptions.MultiplasAvaliacoesIguais;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;

    private final EstacionamentoRepository estacionamentoRepository;

    private final ClienteRepository clienteRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, EstacionamentoRepository estacionamentoRepository, ClienteRepository clienteRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<AvaliacaoDTO> listarAvaliacoes() {
        return avaliacaoRepository.findAll()
                .stream()
                .map(AvaliacaoDTO::fromEntity)
                .toList();
    }

    @Transactional
    public AvaliacaoDTO cadastrarAvaliacao(AvaliacaoDTO dto) {
        Avaliacao avaliacao = dto.toEntity();
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(dto.estacioId());

        validarAvaliacaoUnica(avaliacao);
        avaliacao.validarTamanhoDoComentario();

        if (optCliente.isEmpty() || optEstacio.isEmpty()) {
            throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
        } else {
            avaliacao.setCliente(optCliente.get());
            avaliacao.setEstacionamento(optEstacio.get());
            optEstacio.get().getAvaliacoes().add(avaliacao);

            return AvaliacaoDTO.fromEntity(avaliacaoRepository.save(avaliacao));
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
                Avaliacao avaliacao = optAvaliacao.get();

                avaliacao.validarTamanhoDoComentario();

                avaliacao.setNota(dto.nota());
                avaliacao.setComentario(dto.comentario());
                avaliacao.setDataDeAvaliacao(dto.dataDeAvaliacao());
                avaliacao.setCliente(optCliente.get());
                avaliacao.setEstacionamento(optEstacio.get());

                return AvaliacaoDTO.fromEntity(avaliacaoRepository.save(optAvaliacao.get()));
            }
    }

    @Transactional
    public void deletarAvaliacao(Long id) {
        Optional<Avaliacao> optAvaliacao = avaliacaoRepository.findById(id);

        if (optAvaliacao.isPresent()) {
            avaliacaoRepository.delete(optAvaliacao.get());
            optAvaliacao.get().getEstacionamento().getAvaliacoes().remove(optAvaliacao.get());
        }
    }

    public void validarAvaliacaoUnica(Avaliacao avaliacao) {
        Cliente cliente = avaliacao.getCliente();
        Estacionamento estacionamento = avaliacao.getEstacionamento();

        avaliacaoRepository.findAll()
                .forEach(a -> {
                    if (a.getCliente() == cliente && a.getEstacionamento() == estacionamento) {
                        throw new MultiplasAvaliacoesIguais("O cliente já tem uma avaliação registrada para este" +
                                "estabelecimento");
                    }
                });
    }
}
