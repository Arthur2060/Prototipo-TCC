package com.senai.TCC.model.service;

import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.Reserva;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.ComentarioMuitoLongoException;
import com.senai.TCC.model.exceptions.AvaliacaoInvalidaException;
import com.senai.TCC.model.exceptions.TempoLimiteDeAvaliacaoExpedidoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ValidadorAvaliacao {

    private final AvaliacaoRepository avaliacaoRepository;

    private final ClienteRepository clienteRepository;

    private final EstacionamentoRepository estacionamentoRepository;

    public ValidadorAvaliacao(AvaliacaoRepository avaliacaoRepository,
                              ClienteRepository clienteRepository,
                              EstacionamentoRepository estacionamentoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.clienteRepository = clienteRepository;
        this.estacionamentoRepository = estacionamentoRepository;
    }

    public void validarAvaliacaoAposUso(Avaliacao avaliacao) {
        Cliente cliente = avaliacao.getCliente();
        Estacionamento estacionamento = avaliacao.getEstacionamento();

        if (cliente.getReservas()
                .stream()
                .filter( reserva -> reserva.getEstacionamento().equals(estacionamento))
                .toList()
                .isEmpty()
            ||
            cliente.getCarros()
                    .stream()
                    .filter(
                            carro -> carro.getAcessos()
                                    .stream()
                                    .anyMatch(
                                            acesso -> acesso.getEstacionamento().equals(estacionamento)
                                    )
                    )
                    .toList()
                    .isEmpty()) {
            throw new AvaliacaoInvalidaException("Cliente não possui reserva ou acessos registrados neste estacionamento nesse estacionamento");
        }
    }

    public void validarTempoDeAvaliacao(Avaliacao avaliacao) {
        LocalDateTime dataInical = avaliacao.getDataDeAvaliacao();
        LocalDateTime dataAtual = LocalDateTime.now();

        if (ChronoUnit.DAYS.between(dataInical, dataAtual) >= 7) {
            throw new TempoLimiteDeAvaliacaoExpedidoException("Expedido tempo limite de 7 dias para alterar a avaliação requisitada");
        }
    }


    public void validarTamanhoDoComentario(Avaliacao avaliacao) {
        if (avaliacao.getComentario().length() > 500) {
            throw new ComentarioMuitoLongoException("Comentario muito longo");
        }
    }

    public void validarNumeroDeAvaliacoes(Avaliacao avaliacao) {
        Estacionamento estacionamento = avaliacao.getEstacionamento();
        Cliente cliente = avaliacao.getCliente();

        Integer numeroDeAvaliacoes = estacionamento.getAvaliacoes()
                .stream().filter(avaliacao1 -> avaliacao1.getCliente() == cliente)
                .toList()
                .size();

        if (
                estacionamento.getReservas()
                        .stream().filter(reserva -> reserva.getCliente() == cliente)
                        .toList()
                        .size() <= numeroDeAvaliacoes
        ) {
            throw new AvaliacaoInvalidaException("Mais avalições do que o permitido!");
        }
    }
}
