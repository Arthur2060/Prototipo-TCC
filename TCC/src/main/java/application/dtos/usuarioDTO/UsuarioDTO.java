package application.dtos.usuarioDTO;

import io.swagger.v3.oas.annotations.media.Schema;
import model.entities.usuarios.Cliente;
import model.entities.usuarios.DonoEstacionamento;
import model.entities.usuarios.Gerente;
import model.entities.usuarios.Usuario;
import model.enums.TipoDeUsuario;
import model.exceptions.TipoDeUsuarioInvalido;

import java.util.Date;

public record UsuarioDTO(
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
                name = "Tipo de usuario",
                description = "Um dos quatro tipos de usuario possiveis:" +
                        "CLIENTE, GERENTE ou DONO.",
                examples = "CLENTE"
        )
        TipoDeUsuario tipoDeUsuario
) {
    public Usuario fromDto() {
        Usuario usuario = switch (tipoDeUsuario) {
            case GERENTE -> new Gerente();
            case DONO -> new DonoEstacionamento();
            case CLIENTE -> new Cliente();
        };

        usuario.setNome(nome);
        usuario.setSenha(senha);
        usuario.setEmail(email);
        usuario.setDataNascimento(dataNascimento);

        return usuario;
    }

    public static UsuarioDTO toDTO(Usuario user) {
        TipoDeUsuario tipo = switch (user) {
            case Gerente gerente -> TipoDeUsuario.GERENTE;
            case DonoEstacionamento dono -> TipoDeUsuario.DONO;
            case Cliente cliente -> TipoDeUsuario.CLIENTE;
            default -> throw new TipoDeUsuarioInvalido("O tipo de usuario da classe é invalido!");
        };

        return new UsuarioDTO(
                user.getNome(),
                user.getEmail(),
                user.getSenha(),
                user.getDataNascimento(),
                tipo
        );
    }
}
