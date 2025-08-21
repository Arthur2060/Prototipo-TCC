package application.dtos;

import model.entities.Estacionamento;

import java.io.File;
import java.time.LocalTime;

public record EstacionamentoDTO(
        String nome,
        String endereco,
        File foto,
        String numeroAlvaraDeFuncionamento,
        Boolean status,
        LocalTime horaFechamento,
        LocalTime horaAbertura,
        Integer maximoDeVagas
) {
    public Estacionamento fromDTO() {
        Estacionamento estacionamento = new Estacionamento();

        estacionamento.setNome(nome);
        estacionamento.setEndereco(endereco);
        estacionamento.setFoto(foto);
        estacionamento.setNumeroAlvaraDeFuncionamento(numeroAlvaraDeFuncionamento);
        estacionamento.setStatus(status);
        estacionamento.setHoraFechamento(horaFechamento);
        estacionamento.setHoraAbertura(horaAbertura);
        estacionamento.setMaxVagas(maximoDeVagas);

        return estacionamento;
    }

    public static EstacionamentoDTO toDTO(Estacionamento estacionamento) {
        return new EstacionamentoDTO(
                estacionamento.getNome(),
                estacionamento.getEndereco(),
                estacionamento.getFoto(),
                estacionamento.getNumeroAlvaraDeFuncionamento(),
                estacionamento.getStatus(),
                estacionamento.getHoraFechamento(),
                estacionamento.getHoraAbertura(),
                estacionamento.getMaxVagas()
        );
    }
}
