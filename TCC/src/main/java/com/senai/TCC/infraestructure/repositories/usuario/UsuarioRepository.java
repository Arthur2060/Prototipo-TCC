package com.senai.TCC.infraestructure.repositories.usuario;

import com.senai.TCC.model.entities.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByStatusTrue();
}
