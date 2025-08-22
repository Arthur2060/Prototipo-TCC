package application.dtos.usuarioDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import model.entities.usuarios.Gerente;

import java.util.Date;

public record GerenteDTO(
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
        Date dataNascimento,

        @Schema(
                name = "CPF ou CNPJ",
                description = "Documento oficial necessário para contratar um gerente",
                examples = "123.456.789-10"
        )
        String cpfOuCnpj
) {
    public Gerente fromDTO() {
        Gerente gerente = new Gerente();

        gerente.setNome(nome);
        gerente.setCpfOuCnpj(cpfOuCnpj);
        gerente.setEmail(email);
        gerente.setSenha(senha);
        gerente.setDataNascimento(dataNascimento);

        return gerente;
    }

    public static GerenteDTO toDTO(Gerente gerente) {
        return new GerenteDTO(
                gerente.getNome(),
                gerente.getEmail(),
                gerente.getSenha(),
                gerente.getDataNascimento(),
                gerente.getCpfOuCnpj()
        );
    }
}
