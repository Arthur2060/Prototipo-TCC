package com.senai.TCC.infraestructure.repositories.usuario;

import com.senai.TCC.model.entities.usuarios.DonoEstacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonoRepository extends JpaRepository<DonoEstacionamento, Long> {
}
