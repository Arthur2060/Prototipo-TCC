package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.ValorRequest;
import com.senai.TCC.application.dto.response.ValorResponse;
import com.senai.TCC.application.services.ValorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/valor")
@Tag(
        name = "Valor controller",
        description = "Realiza operações referentes à valores de estacionamento"
)
public class ValorController {
    private final ValorService service;

    public ValorController(ValorService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar valores",
            description = "Lista todos os valores cadastrados no sistema"
    )
    public ResponseEntity<List<ValorResponse>> listarValor() {
        return ResponseEntity.ok(service.listarValor());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma entidade em especifico através de ID",
            tags = {"Valor Controller"}
    )
    public ResponseEntity<ValorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar valor",
            description = "Cadastra um novo valor no sistema"
    )
    public ResponseEntity<ValorResponse> cadastrarValor(ValorRequest dto) {
        return ResponseEntity.ok(service.cadastrarValor(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar valor",
            description = "Atualiza um valor cadastrado no sistema"
    )
    public ResponseEntity<ValorResponse> atualizarValor(@RequestBody ValorRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarValor(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar valor",
            description = "Deleta um valor cadastrado no sistema"
    )
    public void deletarValor(@PathVariable Long id) {
        service.deletarValor(id);
    }
}
