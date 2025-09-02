package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dtos.CarroDTO;
import com.senai.TCC.application.services.CarroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carro")
@Tag(
        name = "Carro controller",
        description = "Realiza operações referentes aos carros"
)
public class CarroController {
    private final CarroService carroService;

    public CarroController(CarroService carroService) {
        this.carroService = carroService;
    }

    @GetMapping
    @Operation(
            summary = "Listar carros",
            description = "Lista todos os carros cadastrados no sistema"
    )
    public ResponseEntity<List<CarroDTO>> listarCarros() {
        return ResponseEntity.ok(carroService.listarCarros());
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar carro",
            description = "Cadastra um novo carro no sistema"
    )
    public ResponseEntity<CarroDTO> cadastrarCarro(@RequestBody CarroDTO dto) {
        return ResponseEntity.ok(carroService.cadastrarCarro(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar carro",
            description = "Atualiza os dados de um carro cadastrado no sistema"
    )
    public ResponseEntity<CarroDTO> atualizarCarro(@RequestBody CarroDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(carroService.atualizarCarro(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar carro",
            description = "Deleta um carro cadastrado no sistema"
    )
    public void deletarCarro(@PathVariable Long id) {
        carroService.deletarCarro(id);
    }
}
