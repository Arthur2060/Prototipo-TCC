package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dtos.ReservaDTO;
import com.senai.TCC.application.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reserva")
@Tag(
        name = "Reserva Controller",
        description = "Realiza operações referentes a criação de reservas"
)
public class ReservaController {
    @Autowired
    private ReservaService service;

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar reservas",
            description = "Cria uma lista de todas as reservas presentes no sistema"
    )
    public ResponseEntity<List<ReservaDTO>> listarReservas() {
        return ResponseEntity.ok(service.listarReservas());
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar reserva",
            description = "Cadastra uma nova reserva no sistema"
    )
    public ResponseEntity<ReservaDTO> cadastrarReserva(@RequestBody ReservaDTO dto) {
        return ResponseEntity.ok(service.cadastrarReserva(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar reserva",
            description = "Altera as informações de uma reserva"
    )
    public ResponseEntity<ReservaDTO> atualizarReserva(@RequestBody ReservaDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarReserva(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar reserva",
            description = "Deleta uma reserva do sistema"
    )
    public void deletarReserva(@PathVariable Long id) {
        service.deletarReserva(id);
    }

}
