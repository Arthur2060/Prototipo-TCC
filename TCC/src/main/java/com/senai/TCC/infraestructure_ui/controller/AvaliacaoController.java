package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.AvaliacaoRequest;
import com.senai.TCC.application.dto.response.AvaliacaoResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
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
    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar todas as avaliações",
            description = "Retorna uma lista de todas as avaliações cadastradas no sistema."
    )
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoes() {
        return ResponseEntity.status(200).body(service.listarAvaliacoes());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma entidade em especifico através de ID"
    )
    public ResponseEntity<AvaliacaoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar nova avaliação",
            description = "Adiciona uma nova avaliação ao sistema"
    )
    public ResponseEntity<AvaliacaoResponse> cadastrarAvaliacao(@RequestBody AvaliacaoRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarAvaliacao(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar uma avaliação",
            description = "Atualiza uma avaliação já cadastrada no sistema"
    )
    public ResponseEntity<AvaliacaoResponse> atualizarAvaliacao(@PathVariable Long id, AvaliacaoRequest dto) {
        return ResponseEntity.status(200).body(service.atualizarAvaliacao(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar uma avaliação",
            description = "Deleta uma avaliação já cadastrada no sistema"
    )
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long id) {
        service.deletarAvaliacao(id);
        return ResponseEntity.status(204).build();
    }
}
