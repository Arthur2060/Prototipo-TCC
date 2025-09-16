package com.senai.TCC.infraestructure.repositories.usuario;

import com.senai.TCC.model.entities.usuarios.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByStatusTrue();
}
