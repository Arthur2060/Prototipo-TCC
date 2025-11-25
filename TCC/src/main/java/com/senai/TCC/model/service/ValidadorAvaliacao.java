package com.senai.TCC.model.service;

import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.ComentarioMuitoLongo;
import com.senai.TCC.model.exceptions.AvaliacaoInvalida;
import com.senai.TCC.model.exceptions.TempoLimiteDeAvaliacaoExpedido;
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

        boolean temReserva = !cliente.getReservas()
                .stream()
                .filter( reserva -> reserva.getEstacionamento().equals(estacionamento))
                .toList()
                .isEmpty();

        boolean temAcesso = !cliente.getCarros()
                .stream()
                .filter(
                        carro -> carro.getAcessos()
                                .stream()
                                .anyMatch(
                                        acesso -> acesso.getEstacionamento().equals(estacionamento)
                                )
                )
                .toList()
                .isEmpty();

        if (!temReserva && !temAcesso) {
            throw new AvaliacaoInvalida("Cliente não possui reserva ou acessos registrados neste estacionamento nesse estacionamento");
        }
    }

    public void validarTempoDeAvaliacao(Avaliacao avaliacao) {
        LocalDateTime dataInical = avaliacao.getDataDeAvaliacao();
        LocalDateTime dataAtual = LocalDateTime.now();

        if (ChronoUnit.DAYS.between(dataInical, dataAtual) >= 7) {
            throw new TempoLimiteDeAvaliacaoExpedido("Expedido tempo limite de 7 dias para alterar a avaliação requisitada");
        }
    }


    public void validarTamanhoDoComentario(Avaliacao avaliacao) {
        if (avaliacao.getComentario().length() > 500) {
            throw new ComentarioMuitoLongo("Comentario muito longo");
        }
    }

//    public void validarNumeroDeAvaliacoes(Avaliacao avaliacao) {
//        Estacionamento estacionamento = avaliacao.getEstacionamento();
//        Cliente cliente = avaliacao.getCliente();
//
//        Integer numeroDeAvaliacoes = estacionamento.getAvaliacoes()
//                .stream().filter(avaliacao1 -> avaliacao1.getCliente() == cliente)
//                .toList()
//                .size();
//
//        if (
//                estacionamento.getReservas()
//                        .stream().filter(reserva -> reserva.getCliente() == cliente)
//                        .toList()
//                        .size() <= numeroDeAvaliacoes
//        ) {
//            throw new AvaliacaoInvalida("Mais avalições do que o permitido!");
//        }
//    }
}
