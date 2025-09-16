package com.senai.TCC.application.mappers;

import com.senai.TCC.application.dto.create_requests.EstacionamentoCreateRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.model.entities.Estacionamento;

public class EstacionamentoMapper {

    public static  EstacionamentoResponse fromEntity(Estacionamento estacionamento) {
        return new EstacionamentoResponse(
                estacionamento.getId(),
                estacionamento.getNome(),
                estacionamento.getEndereco(),
                estacionamento.getCEP(),
                estacionamento.getNumero(),
                estacionamento.getFoto(),
                estacionamento.getNumeroDeEscrituraImovel(),
                estacionamento.getHoraFechamento(),
                estacionamento.getHoraAbertura(),
                estacionamento.getVagaPreferenciais(),
                estacionamento.getMaxVagas(),
                estacionamento.getNumeroDeEscrituraImovel(),
                estacionamento.getStatus()
        );
    }

    public static Estacionamento toEntity(EstacionamentoCreateRequest dto) {
        Estacionamento estacionamento = new Estacionamento();

        estacionamento.setNome(dto.nome());
        estacionamento.setEndereco(dto.endereco());
        estacionamento.setCEP(dto.CEP());
        estacionamento.setNumero(dto.numero());
        estacionamento.setFoto(dto.foto());
        estacionamento.setNumeroAlvaraDeFuncionamento(dto.numeroAlvaraDeFuncionamento());
        estacionamento.setHoraFechamento(dto.horaFechamento());
        estacionamento.setHoraAbertura(dto.horaAbertura());
        estacionamento.setVagaPreferenciais(dto.vagasPreferenciais());
        estacionamento.setMaxVagas(dto.maximoDeVagas());
        estacionamento.setNumeroDeEscrituraImovel(dto.numeroDeEscrituraImovel());

        return estacionamento;
    }
}
