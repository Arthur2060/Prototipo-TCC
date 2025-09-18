package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByEstacionamentoId(Long estacionamentoId);
    List<Reserva> findByStatusTrue();
}
