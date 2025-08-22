package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Valor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValorRepository extends JpaRepository<Valor, Long> {
}
