package application.dtos;

import model.entities.Avaliacao;

import java.time.LocalDateTime;

public record AvaliacaoDTO(
        Short nota,
        String comentario,
        LocalDateTime dataDeAvaliacao
) {
    public Avaliacao fromDTO() {
        Avaliacao avaliacao = new Avaliacao();

        avaliacao.setDataDeAvaliacao(dataDeAvaliacao);
        avaliacao.setComentario(comentario);
        avaliacao.setNota(nota);

        return avaliacao;
    }

    public static AvaliacaoDTO toDTO(Avaliacao avaliacao) {
        return new AvaliacaoDTO(
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataDeAvaliacao()
        );
    }
}
