package com.senai.TCC.infraestructure_ui.controller.usuario;

import com.senai.TCC.application.dto.create_requests.usuario.GerenteCreateRequest;
import com.senai.TCC.application.dto.response.usuario.GerenteResponse;
import com.senai.TCC.application.services.usuario.GerenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gerente")
@Tag(
        name = "Gerente controller",
        description = "Realiza operações relacionadas aos gerentes de estacionamento"
)
public class GerenteController {
    private final GerenteService service;

    public GerenteController(GerenteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os gerentes",
            description = "Retorna uma lista de todos os gerentes cadastrados no sistema."
    )
    public ResponseEntity<List<GerenteResponse>> listarGerentes() {
        return ResponseEntity.ok(service.listarGerentes());
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar um novo gerente",
            description = "Cadastra um novo gerente no sistema com base nos dados fornecidos.",
            tags = {"Gerente controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Gerente cadastrado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida"
                    )
            }
    )
    public ResponseEntity<GerenteResponse> cadastrarGerente(@RequestBody GerenteCreateRequest dto) {
        return ResponseEntity.ok(service.cadastrarGerente(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar um gerente existente",
            description = "Atualiza os dados de um gerente existente com base no ID fornecido.",
            tags = {"Gerente controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Gerente atualizado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Gerente não encontrado"
                    )
            }
    )
    public ResponseEntity<GerenteResponse> atualizarGerente(@RequestBody GerenteCreateRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarGerente(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar um gerente",
            description = "Deleta um gerente existente com base no ID fornecido.",
            tags = {"Gerente controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Gerente deletado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Gerente não encontrado"
                    )
            }
    )
    public void deletarGerente(@PathVariable Long id) {
        service.deletarGerente(id);
    }
}
