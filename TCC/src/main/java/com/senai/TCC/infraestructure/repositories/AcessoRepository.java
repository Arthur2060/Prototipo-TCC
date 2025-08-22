package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Acesso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcessoRepository extends JpaRepository<Acesso, Long> {
}
