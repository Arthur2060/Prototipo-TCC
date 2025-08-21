package application.dtos;

import model.entities.Estacionamento;

import java.io.File;
import java.time.LocalTime;

public record EstacionamentoDTO(
        String nome,
        String endereco,
        String CEP,
        String numero,
        File foto,
        String numeroAlvaraDeFuncionamento,
        LocalTime horaFechamento,
        LocalTime horaAbertura,
        Integer vagasPreferenciais,
        Integer maximoDeVagas
) {
    public Estacionamento fromDTO() {
        Estacionamento estacionamento = new Estacionamento();

        estacionamento.setNome(nome);
        estacionamento.setEndereco(endereco);
        estacionamento.setCEP(CEP);
        estacionamento.setNumero(numero);
        estacionamento.setFoto(foto);
        estacionamento.setNumeroAlvaraDeFuncionamento(numeroAlvaraDeFuncionamento);
        estacionamento.setHoraFechamento(horaFechamento);
        estacionamento.setHoraAbertura(horaAbertura);
        estacionamento.setVagaPreferenciais(vagasPreferenciais);
        estacionamento.setMaxVagas(maximoDeVagas);

        return estacionamento;
    }

    public static EstacionamentoDTO toDTO(Estacionamento estacionamento) {
        return new EstacionamentoDTO(
                estacionamento.getNome(),
                estacionamento.getEndereco(),
                estacionamento.getCEP(),
                estacionamento.getNumero(),
                estacionamento.getFoto(),
                estacionamento.getNumeroAlvaraDeFuncionamento(),
                estacionamento.getHoraFechamento(),
                estacionamento.getHoraAbertura(),
                estacionamento.getVagaPreferenciais(),
                estacionamento.getMaxVagas()
        );
    }
}
