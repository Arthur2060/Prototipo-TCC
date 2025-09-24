package com.senai.TCC.infraestructure.repositories.usuario;

import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonoRepository extends JpaRepository<DonoEstacionamento, Long> {
    List<DonoEstacionamento> findByStatusTrue();
    Optional<DonoEstacionamento> findByEmail(String email);
}
