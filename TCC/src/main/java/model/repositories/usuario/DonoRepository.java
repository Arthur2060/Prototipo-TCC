package model.repositories.usuario;

import model.entities.usuarios.DonoEstacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonoRepository extends JpaRepository<DonoEstacionamento, Long> {
}
