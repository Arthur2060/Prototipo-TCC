package com.senai.TCC.application.mappers;

import com.senai.TCC.application.dto.requests.EstacionamentoRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.mappers.usuario.GerenteMapper;
import com.senai.TCC.model.entities.Estacionamento;

import java.util.List;

public class EstacionamentoMapper {

    public static  EstacionamentoResponse fromEntity(Estacionamento estacionamento) {
        return new EstacionamentoResponse(
                estacionamento.getId(),
                estacionamento.getNome(),
                estacionamento.getEndereco(),
                estacionamento.getCEP(),
                estacionamento.getNumero(),
                estacionamento.getFotoUrl(),
                estacionamento.getNumeroDeEscrituraImovel(),
                estacionamento.getHoraFechamento(),
                estacionamento.getHoraAbertura(),
                estacionamento.getVagasPreferenciais(),
                estacionamento.getMaxVagas(),
                estacionamento.getNumeroDeEscrituraImovel(),
                estacionamento.getStatus(),
                estacionamento.getAvaliacoes() != null ?
                        estacionamento.getAvaliacoes().stream()
                                .map(AvaliacaoMapper::fromEntity)
                                .toList() : List.of(),
                estacionamento.getAcessos() != null ?
                        estacionamento.getAcessos().stream()
                                .map(AcessoMapper::fromEntity)
                                .toList() : List.of(),
                estacionamento.getReservas() != null ?
                        estacionamento.getReservas().stream()
                                .map(ReservaMapper::resumo)
                                .toList() : List.of(),
                estacionamento.getGerentes() != null ?
                        estacionamento.getGerentes().stream()
                                .map(GerenteMapper::fromEntity)
                                .toList() : List.of()
        );
    }

    public static Estacionamento toEntity(EstacionamentoRequest dto) {
        Estacionamento estacionamento = new Estacionamento();

        estacionamento.setNome(dto.nome());
        estacionamento.setEndereco(dto.endereco());
        estacionamento.setCEP(dto.CEP());
        estacionamento.setNumero(dto.numero());
        estacionamento.setFotoUrl(dto.foto());
        estacionamento.setNumeroAlvaraDeFuncionamento(dto.numeroAlvaraDeFuncionamento());
        estacionamento.setHoraFechamento(dto.horaFechamento());
        estacionamento.setHoraAbertura(dto.horaAbertura());
        estacionamento.setVagasPreferenciais(dto.vagasPreferenciais());
        estacionamento.setMaxVagas(dto.maximoDeVagas());
        estacionamento.setNumeroDeEscrituraImovel(dto.numeroDeEscrituraImovel());

        return estacionamento;
    }
}
