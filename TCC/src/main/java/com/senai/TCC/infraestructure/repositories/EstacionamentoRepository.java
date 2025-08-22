package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {
}
