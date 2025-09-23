package com.senai.TCC.model.service;

import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.ComentarioMuitoLongoException;
import com.senai.TCC.model.exceptions.AvaliacaoInvalidaException;
import com.senai.TCC.model.exceptions.TempoLimiteDeAvaliacaoExpedidoException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
            throw new TempoLimiteDeAvaliacaoExpedidoException("Espedido tempo limite de 7 dias para alterar a avaliação requisitada");
        }
    }


    public void validarTamanhoDoComentario(Avaliacao avaliacao) {
        if (avaliacao.getComentario().length() > 500) {
            throw new ComentarioMuitoLongoException("Comentario muito longo");
        }
    }
}
