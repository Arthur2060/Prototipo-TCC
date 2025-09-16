package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByEstacionamentoId(Long estacionamentoId);
    List<Avaliacao> findByClienteId(Long clienteId);
    List<Avaliacao> findByStatusTrue();
}
