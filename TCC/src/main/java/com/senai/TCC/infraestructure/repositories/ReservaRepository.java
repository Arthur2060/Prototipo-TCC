package com.senai.TCC.infraestructure.repositories;

import com.senai.TCC.model.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
