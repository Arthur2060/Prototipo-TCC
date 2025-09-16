package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.create_requests.AvaliacaoCreateRequest;
import com.senai.TCC.application.mappers.AvaliacaoMapper;
import com.senai.TCC.application.dto.response.AvaliacaoResponse;
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

    public List<AvaliacaoResponse> listarAvaliacoes() {
        return avaliacaoRepository.findAll()
                .stream()
                .map(AvaliacaoMapper::fromEntity)
                .toList();
    }

    @Transactional
    public AvaliacaoResponse cadastrarAvaliacao(AvaliacaoCreateRequest dto) {
        Avaliacao avaliacao = AvaliacaoMapper.toEntity(dto);
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(dto.estacioId());

        validarAvaliacaoUnica(avaliacao);
        avaliacao.validarTamanhoDoComentario();

        if (optCliente.isEmpty() || optEstacio.isEmpty()) {
            throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
        } else {
            Cliente cliente = optCliente.get();
            Estacionamento estacionamento = optEstacio.get();

            avaliacao.setCliente(cliente);
            avaliacao.setEstacionamento(estacionamento);
            estacionamento.getAvaliacoes().add(avaliacao);
            cliente.getAvaliacoes().add(avaliacao);

            estacionamento.calcularNotaMedia();

            avaliacao.setStatus(true);
            return AvaliacaoMapper.fromEntity(avaliacaoRepository.save(avaliacao));
        }
    }

    @Transactional
    public AvaliacaoResponse atualizarAvaliacao(AvaliacaoCreateRequest dto, Long id) {
        Optional<Avaliacao> optAvaliacao = avaliacaoRepository.findById(id);
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(dto.estacioId());

        if (optAvaliacao.isEmpty()) {
            throw new IdNaoCadastrado("A avaliação buscada não existe no sistema");
        } else if (optCliente.isEmpty() || optEstacio.isEmpty()) {
                throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
            } else {
                Estacionamento estacionamento = optEstacio.get();
                Cliente cliente = optCliente.get();
                Avaliacao avaliacao = optAvaliacao.get();

                avaliacao.validarTamanhoDoComentario();

                avaliacao.setNota(dto.nota());
                avaliacao.setComentario(dto.comentario());
                avaliacao.setDataDeAvaliacao(dto.dataDeAvaliacao());
                avaliacao.setCliente(cliente);
                avaliacao.setEstacionamento(estacionamento);

                estacionamento.calcularNotaMedia();

                return AvaliacaoMapper.fromEntity(avaliacaoRepository.save(optAvaliacao.get()));
            }
    }

    @Transactional
    public void deletarAvaliacao(Long id) {
        Optional<Avaliacao> optAvaliacao = avaliacaoRepository.findById(id);

        if (optAvaliacao.isPresent()) {
            Avaliacao avaliacao = optAvaliacao.get();

            Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(avaliacao.getEstacionamento().getId());
            Optional<Cliente> optCliente = clienteRepository.findById(avaliacao.getCliente().getId());
            if (optCliente.isEmpty() || optEstacio.isEmpty()) {
                throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
            }

            Estacionamento estacionamento = optEstacio.get();
            Cliente cliente = optCliente.get();

            estacionamento.getAvaliacoes().remove(avaliacao);
            cliente.getAvaliacoes().remove(avaliacao);

            avaliacao.setStatus(false);
            avaliacaoRepository.save(avaliacao);
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
