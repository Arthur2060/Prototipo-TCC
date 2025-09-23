package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.CarroRequest;
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
    private final CarroService service;

    public CarroController(CarroService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar carros",
            description = "Lista todos os carros cadastrados no sistema"
    )
    public ResponseEntity<List<CarroResponse>> listarCarros() {
        return ResponseEntity.status(200).body(service.listarCarros());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma entidade em especifico através de ID"
    )
    public ResponseEntity<CarroResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar carro",
            description = "Cadastra um novo carro no sistema"
    )
    public ResponseEntity<CarroResponse> cadastrarCarro(@RequestBody CarroRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarCarro(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar carro",
            description = "Atualiza os dados de um carro cadastrado no sistema"
    )
    public ResponseEntity<CarroResponse> atualizarCarro(@RequestBody CarroRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarCarro(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar carro",
            description = "Deleta um carro cadastrado no sistema"
    )
    public ResponseEntity<Void> deletarCarro(@PathVariable Long id) {
        service.deletarCarro(id);
        return ResponseEntity.status(204).build();
    }
}
