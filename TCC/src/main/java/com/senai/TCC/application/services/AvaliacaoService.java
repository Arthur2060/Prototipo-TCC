package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.requests.AvaliacaoRequest;
import com.senai.TCC.application.mappers.AvaliacaoMapper;
import com.senai.TCC.application.dto.response.AvaliacaoResponse;
import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import com.senai.TCC.model.service.ValidadorAvaliacao;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;

    private final EstacionamentoRepository estacionamentoRepository;

    private final ClienteRepository clienteRepository;

    private final ValidadorAvaliacao validador;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            EstacionamentoRepository estacionamentoRepository,
                            ClienteRepository clienteRepository,
                            ValidadorAvaliacao validador) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.clienteRepository = clienteRepository;
        this.validador = validador;
    }

    public List<AvaliacaoResponse> listarAvaliacoes() {
        return avaliacaoRepository.findByStatusTrue()
                .stream()
                .map(AvaliacaoMapper::fromEntity)
                .toList();
    }

    public AvaliacaoResponse buscarPorId(Long id) {
        Optional<Avaliacao> optionalAvaliacao = avaliacaoRepository.findById(id);

        if (optionalAvaliacao.isEmpty()) {
            throw new IdNaoCadastrado("ID buscado não foi encontrado no sistema!");
        }

        return AvaliacaoMapper.fromEntity(optionalAvaliacao.get());
    }

    @Transactional
    public AvaliacaoResponse cadastrarAvaliacao(AvaliacaoRequest dto) {
        Avaliacao avaliacao = AvaliacaoMapper.toEntity(dto);
        Optional<Cliente> optCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optEstacio = estacionamentoRepository.findById(dto.estacioId());

        if (optCliente.isEmpty() || optEstacio.isEmpty()) {
            throw new IdNaoCadastrado("Cliente ou estacionamento não encontrado no sistema");
        } else {
            Cliente cliente = optCliente.get();
            Estacionamento estacionamento = optEstacio.get();

            validador.validarNumeroDeAvaliacoes(avaliacao);
            validador.validarAvaliacaoAposUso(avaliacao);
            validador.validarTamanhoDoComentario(avaliacao);

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
    public AvaliacaoResponse atualizarAvaliacao(AvaliacaoRequest dto, Long id) {
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

                validador.validarAvaliacaoAposUso(avaliacao);
                validador.validarTamanhoDoComentario(avaliacao);
                validador.validarTempoDeAvaliacao(avaliacao);

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

        if (optAvaliacao.isEmpty()) {
            throw new IdNaoCadastrado("Não foi possivel encontrar a avalição buscada");
        }
        Avaliacao avaliacao = optAvaliacao.get();

        validador.validarAvaliacaoAposUso(avaliacao);

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
