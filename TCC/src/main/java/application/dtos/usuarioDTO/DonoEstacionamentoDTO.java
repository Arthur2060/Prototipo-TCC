package application.dtos.usuarioDTO;

import model.entities.usuarios.DonoEstacionamento;

import java.util.Date;
import java.util.List;

public record DonoEstacionamentoDTO(
        String nome,
        String email,
        String senha,
        Date dataNascimento
) {
    public DonoEstacionamento fromDTO() {
        DonoEstacionamento dono = new DonoEstacionamento();

        dono.setNome(nome);
        dono.setEmail(email);
        dono.setSenha(senha);
        dono.setDataNascimento(dataNascimento);

        return dono;
    }

    public static DonoEstacionamentoDTO toDTO(DonoEstacionamento dono) {
        return new DonoEstacionamentoDTO(
                dono.getNome(),
                dono.getEmail(),
                dono.getSenha(),
                dono.getDataNascimento()
        );
    }
}
