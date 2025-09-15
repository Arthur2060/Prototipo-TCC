package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.ValorDTO;
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
    private final ValorService valorService;

    public ValorController(ValorService valorService) {
        this.valorService = valorService;
    }

    @GetMapping
    @Operation(
            summary = "Listar valores",
            description = "Lista todos os valores cadastrados no sistema"
    )
    public ResponseEntity<List<ValorDTO>> listarValor() {
        return ResponseEntity.ok(valorService.listarValor());
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar valor",
            description = "Cadastra um novo valor no sistema"
    )
    public ResponseEntity<ValorDTO> cadastrarValor(ValorDTO dto) {
        return ResponseEntity.ok(valorService.cadastrarValor(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar valor",
            description = "Atualiza um valor cadastrado no sistema"
    )
    public ResponseEntity<ValorDTO> atualizarValor(@RequestBody ValorDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(valorService.atualizarValor(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar valor",
            description = "Deleta um valor cadastrado no sistema"
    )
    public void deletarValor(@PathVariable Long id) {
        valorService.deletarValor(id);
    }
}
