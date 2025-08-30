package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dtos.CarroDTO;
import com.senai.TCC.application.services.CarroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carro")
@Tag(
        name = "Carro controller",
        description = "Realiza operações referentes ao CRUD de carros"
)
public class CarroController {
    @Autowired
    private CarroService service;

    @GetMapping
    public ResponseEntity<List<CarroDTO>> listarCarros() {
        return ResponseEntity.ok(service.listarCarros());
    }

    @PostMapping
    public ResponseEntity<CarroDTO> cadastrarCarro(@RequestBody CarroDTO dto) {
        return ResponseEntity.ok(service.cadastrarCarro(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarroDTO> atualizarCarro(@RequestBody CarroDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarCarro(dto, id));
    }

    @DeleteMapping("/{id}")
    public void deletarCarro(@PathVariable Long id) {
        service.deletarCarro(id);
    }
}
