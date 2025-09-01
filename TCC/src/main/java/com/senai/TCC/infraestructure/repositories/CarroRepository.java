package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Carro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarroRepository extends JpaRepository<Carro, Long> {
}
