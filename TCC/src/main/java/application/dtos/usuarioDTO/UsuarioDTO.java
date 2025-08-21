package application.dtos.usuarioDTO;

import model.entities.usuarios.Cliente;
import model.entities.usuarios.DonoEstacionamento;
import model.entities.usuarios.Gerente;
import model.entities.usuarios.Usuario;
import model.enums.TipoDeUsuario;
import model.exceptions.TipoDeUsuarioInvalido;

import java.util.Date;

public record UsuarioDTO(
        String nome,
        String email,
        String senha,
        Date dataNascimento,
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
