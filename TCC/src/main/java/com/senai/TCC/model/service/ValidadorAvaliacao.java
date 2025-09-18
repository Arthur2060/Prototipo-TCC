package com.senai.TCC.model.service;

import com.senai.TCC.infraestructure.repositories.AvaliacaoRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Avaliacao;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.ComentarioMuitoLongo;
import com.senai.TCC.model.exceptions.MultiplasAvaliacoesIguais;
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

    public void validarTempoDeAvaliacao(Avaliacao avaliacao) {
        LocalDateTime dataInical = avaliacao.getDataDeAvaliacao();
        LocalDateTime dataAtual = LocalDateTime.now();

        if (ChronoUnit.DAYS.between(dataInical, dataAtual) >= 7) {
            throw new TempoLimiteDeAvaliacaoExpedido("Espedido tempo limite de 7 dias para alterar a avaliação requisitada");
        }
    }


    public void validarTamanhoDoComentario(Avaliacao avaliacao) {
        if (avaliacao.getComentario().length() > 500) {
            throw new ComentarioMuitoLongo("Comentario muito longo");
        }
    }
}
