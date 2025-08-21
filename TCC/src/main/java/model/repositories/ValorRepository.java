package model.repositories;

import model.entities.Valor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValorRepository extends JpaRepository<Valor, Long> {
}
