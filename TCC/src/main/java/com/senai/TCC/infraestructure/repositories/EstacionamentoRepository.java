package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Estacionamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstacionamentoRepository extends JpaRepository<Estacionamento, Long> {
    List<Estacionamento> findByStatusTrue();
    List<Estacionamento> findByDonoEstacionamento_Id(Long donoId);
}
