package com.senai.TCC.infraestructure.repositories.usuario;

import com.senai.TCC.model.entities.usuarios.Gerente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GerenteRepository extends JpaRepository<Gerente, Long> {
    List<Gerente> findByStatusTrue();
    Optional<Gerente> findByEmail(String email);
}
