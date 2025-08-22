package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
}
