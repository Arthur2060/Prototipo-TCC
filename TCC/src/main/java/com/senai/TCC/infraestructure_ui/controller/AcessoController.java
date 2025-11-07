package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.dto.response.AcessoResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
import com.senai.TCC.application.services.AcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/acesso")
@Tag(
        name = "Acesso controller",
        description = "Realiza operações referente ao acesso dos carros aos estacionamentos"
)
public class AcessoController {
    private final AcessoService service;

    public AcessoController(AcessoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar todos os acessos",
            description = "Retorna uma lista de todos os acessos cadastrados no sistema."
    )
    public ResponseEntity<List<AcessoResponse>> listarAcessos() {
        return ResponseEntity.status(200).body(service.listarAcessos());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma entidade em especifico através de ID"
    )
    public ResponseEntity<AcessoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar novo acesso",
            description = "Adiciona um novo acesso ao sistema",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do acesso a ser cadastrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AcessoRequest.class),
                            examples = @ExampleObject(value = """
                                    { "placaDoCarro": "string","horaDeEntrada": "09:38:51","horaDeSaida": "19:38:51","valorAPagar": 0,"estacioId": 1 }
                                    """
                            )
                    )
            )
    )
    public ResponseEntity<AcessoResponse> cadastrarAcesso(@RequestBody AcessoRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarAcesso(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar um acesso",
            description = "Atualiza um acesso já cadastrado no sistema"
    )
    public ResponseEntity<AcessoResponse> atualizarAcesso(@PathVariable Long id, @RequestBody AcessoRequest dto) {
        return ResponseEntity.status(200).body(service.atualizarAcesso(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar um acesso",
            description = "Deleta um acesso já cadastrado no sistema"
    )
    public ResponseEntity<Void> deletarAcesso(@PathVariable Long id) {
        service.deletarAcesso(id);
        return ResponseEntity.status(204).build();
    }
}
