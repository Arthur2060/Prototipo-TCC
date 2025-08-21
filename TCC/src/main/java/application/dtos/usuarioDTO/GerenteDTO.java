package application.dtos.usuarioDTO;

import model.entities.usuarios.Gerente;

import java.util.Date;

public record GerenteDTO(
        String nome,
        String email,
        String senha,
        Date dataNascimento,
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
