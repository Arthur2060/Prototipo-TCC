package model.repositories.usuario;

import model.entities.usuarios.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {
}
