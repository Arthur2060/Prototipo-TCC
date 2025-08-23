package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dtos.AcessoDTO;
import com.senai.TCC.application.services.AcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Acesso")
@Tag(
        name = "Acesso",
        description = "Realiza operações referente ao acesso dos carros aos estacionamentos"
)
public class AcessoController {
    @Autowired
    private AcessoService service;

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar todos os acessos",
            description = "Retorna uma lista de todos os acessos cadastrados no sistema.",
            tags = {"Acesso"}
    )
    public ResponseEntity<List<AcessoDTO>> listarAcessos() {
        return ResponseEntity.ok(service.listarAcessos());
    }

    @PostMapping("/{id}")
    @Operation(
            method = "POST",
            summary = "Cadastrar novo acesso",
            description = "Adiciona um novo acesso ao sistema",
            tags = {"Acesso"}
    )
    public ResponseEntity<AcessoDTO> cadastrarAcesso(@RequestBody AcessoDTO dto) {
        return ResponseEntity.ok(service.cadastrarAcesso(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar um acesso",
            description = "Atualiza um acesso já cadastrado no sistema",
            tags = {"Acesso"}
    )
    public ResponseEntity<AcessoDTO> atualizarAcesso(@PathVariable Long acessoId, @RequestBody AcessoDTO dto) {
        return ResponseEntity.ok(service.atualizarAcesso(dto, acessoId));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar um acesso",
            description = "Deleta um acesso já cadastrado no sistema",
            tags = {"Acesso"}
    )
    public void deletarAcesso(@PathVariable Long id) {
        service.deletarAcesso(id);
    }
}
