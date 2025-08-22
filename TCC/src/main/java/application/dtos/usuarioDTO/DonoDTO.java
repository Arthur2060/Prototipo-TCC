package application.dtos.usuarioDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import model.entities.usuarios.DonoEstacionamento;

import java.util.Date;

public record DonoDTO(
        @Schema(
                name = "Nome",
                description = "Nome do usuario.",
                examples = "Pedro"
        )
        String nome,

        @Schema(
                name = "email",
                description = "E-mail do usuario",
                examples = "pedro.nascimento@gmail.com"
        )
        String email,

        @Schema(
                name = "Senha",
                description = "Senha de acesso do usuario, " +
                        "deve conter pelo menos uma letra maiuscula, simbolo e numero",
                examples = "Estacio_2025"
        )
        String senha,

        @Schema(
                name = "Data de nascimento",
                description = "Data de nascimento do usuario, deve ser maior de 18 anos",
                examples = "18-09-2000"
        )
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
