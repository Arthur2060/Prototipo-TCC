package application.dtos.usuarioDTO;

import model.entities.usuarios.Cliente;

import java.util.Date;

public record ClienteDTO(
        String nome,
        String email,
        String senha,
        Date dataNascimento,
        String placaDoCarro
) {
    public Cliente fromDto() {
        Cliente cliente = new Cliente();

        cliente.setNome(nome);
        cliente.setPlacaDoCarro(placaDoCarro);
        cliente.setEmail(email);
        cliente.setDataNascimento(dataNascimento);
        cliente.setSenha(senha);

        return cliente;
    }

    public static ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getSenha(),
                cliente.getDataNascimento(),
                cliente.getPlacaDoCarro()
        );
    }
}
