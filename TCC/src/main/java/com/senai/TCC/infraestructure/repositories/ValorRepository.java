package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Valor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValorRepository extends JpaRepository<Valor, Long> {
    List<Valor> findByEstacionamentoId(Long estacionamentoId);
    List<Valor> findByStatusTrue();
}
