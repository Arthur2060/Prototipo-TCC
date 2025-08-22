package application.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import model.entities.Estacionamento;

import java.io.File;
import java.time.LocalTime;

public record EstacionamentoDTO(
        @Schema(
                name = "Nome",
                description = "Nome do estacionamento.",
                examples = "Estacionamento Central"
        )
        String nome,
        @Schema(
                name = "Endereço",
                description = "Endereço do estacionamento.",
                examples = "Rua das Flores"
        )
        String endereco,
        @Schema(
                name = "CEP",
                description = "CEP do estacionamento.",
                examples = "12345-678"
        )
        String CEP,
        @Schema(
                name = "Número",
                description = "Número do estacionamento.",
                examples = "123"
        )
        String numero,
        @Schema(
                name = "Foto",
                description = "Foto do estacionamento.",
                examples = "foto.jpg"
        )
        File foto,
        @Schema(
                name = "Número do alvará de funcionamento",
                description = "Número do alvará de funcionamento do estacionamento.",
                examples = "123456789"
        )
        String numeroAlvaraDeFuncionamento,
        @Schema(
                name = "Hora de fechamento",
                description = "Hora de fechamento do estacionamento.",
                examples = "22:00"
        )
        LocalTime horaFechamento,
        @Schema(
                name = "Hora de abertura",
                description = "Hora de abertura do estacionamento.",
                examples = "08:00"
        )
        LocalTime horaAbertura,
        @Schema(
                name = "Vagas preferenciais",
                description = "Número de vagas preferenciais do estacionamento.",
                examples = "10"
        )
        Integer vagasPreferenciais,
        @Schema(
                name = "Máximo de vagas",
                description = "Número máximo de vagas do estacionamento.",
                examples = "100"
        )
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
