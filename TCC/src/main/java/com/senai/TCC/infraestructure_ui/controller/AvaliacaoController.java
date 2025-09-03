package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dtos.AvaliacaoDTO;
import com.senai.TCC.application.services.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Avaliacao")
@Tag(
        name = "Avaliação controller",
        description = "Realiza operações referentes à avaliações de estacionamento"
)
public class AvaliacaoController {
    private AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar todas as avaliações",
            description = "Retorna uma lista de todas as avaliações cadastradas no sistema."
    )
    public ResponseEntity<List<AvaliacaoDTO>> listarAvaliacoes() {
        return ResponseEntity.ok(service.listarAvaliacoes());
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar nova avaliação",
            description = "Adiciona uma nova avaliação ao sistema"
    )
    public ResponseEntity<AvaliacaoDTO> cadastrarAvaliacao(AvaliacaoDTO dto) {
        return ResponseEntity.ok(service.cadastrarAvaliacao(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar uma avaliação",
            description = "Atualiza uma avaliação já cadastrada no sistema"
    )
    public ResponseEntity<AvaliacaoDTO> atualizarAvaliacao(@PathVariable Long avaliacaoId, AvaliacaoDTO dto) {
        return ResponseEntity.ok(service.atualizarAvaliacao(dto, avaliacaoId));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar uma avaliação",
            description = "Deleta uma avaliação já cadastrada no sistema"
    )
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long avaliacaoId) {
        service.deletarAvaliacao(avaliacaoId);
        return ResponseEntity.noContent().build();
    }
}
