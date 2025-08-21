package application.dtos.usuarioDTO;

import model.entities.usuarios.DonoEstacionamento;

import java.util.Date;

public record DonoDTO(
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

    public static DonoDTO toDTO(DonoEstacionamento dono) {
        return new DonoDTO(
                dono.getNome(),
                dono.getEmail(),
                dono.getSenha(),
                dono.getDataNascimento()
        );
    }
}
