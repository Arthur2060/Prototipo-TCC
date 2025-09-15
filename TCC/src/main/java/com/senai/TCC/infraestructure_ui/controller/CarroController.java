package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.request.CarroCreateRequest;
import com.senai.TCC.application.dto.response.CarroResponse;
import com.senai.TCC.application.services.CarroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<List<CarroResponse>> listarCarros() {
        return ResponseEntity.ok(carroService.listarCarros());
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar carro",
            description = "Cadastra um novo carro no sistema"
    )
    public ResponseEntity<CarroResponse> cadastrarCarro(@RequestBody CarroCreateRequest dto) {
        return ResponseEntity.ok(carroService.cadastrarCarro(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar carro",
            description = "Atualiza os dados de um carro cadastrado no sistema"
    )
    public ResponseEntity<CarroResponse> atualizarCarro(@RequestBody CarroCreateRequest dto, @PathVariable Long id) {
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
