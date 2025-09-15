package com.senai.TCC.infraestructure_ui.controller;

import com.senai.TCC.application.dto.request.EstacionamentoCreateRequest;
import com.senai.TCC.application.dto.response.EstacionamentoResponse;
import com.senai.TCC.application.services.EstacionamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estacionamento")
@Tag(
        name = "Estacionamento controller",
        description = "Realiza operações referentes aos estacionamentos"
)
public class EstacionamentoController {
    private final EstacionamentoService service;

    public EstacionamentoController(EstacionamentoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar todos os estacionamentos",
            description = "Retorna uma lista de todos os estacionamentos cadastrados no sistema.",
            tags = {"Estacionamento controller"}
    )
    public ResponseEntity<List<EstacionamentoResponse>> listarEstacionamentos() {
        return ResponseEntity.ok(service.listarTodosOsEstacionamentos());
    }

    @PostMapping("/{id}")
    @Operation(
            method = "POST",
            summary = "Cadastrar um novo estacionamento",
            description = "Cadastra um novo estacionamento no sistema com base nos dados fornecidos.",
            tags = {"Estacionamento controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Estacionamento cadastrado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida"
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados do estacionamento a ser cadastrado",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = EstacionamentoCreateRequest.class),
                            examples = @ExampleObject(value = """
                                  {
                                    "nome": "string",
                                    "endereco": "string",
                                    "CEP": "string",
                                    "numero": "string",
                                    "foto": "file",
                                    "numeroAlvaraDeFuncionamento": "string",
                                    "horaFechamento": "22:22:22",
                                    "horaAbertura": "22:22:22",
                                    "vagasPreferenciais": 0,
                                    "maximoDeVagas": 0,
                                    "numeroDeEscrituraImovel": "string"
                                  }
                                  """
                            )
                    )
            )
    )
    public ResponseEntity<EstacionamentoResponse> cadastrarEstacionamento(@RequestBody EstacionamentoCreateRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.cadastrarEstacionamento(dto, id));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar um estacionamento existente",
            description = "Atualiza os dados de um estacionamento existente com base no ID fornecido.",
            tags = {"Estacionamento controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Estacionamento atualizado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Estacionamento não encontrado"
                    )
            }
    )
    public ResponseEntity<EstacionamentoResponse> atualizarEstacionamento(@RequestBody EstacionamentoCreateRequest dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarEstacionamento(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Desativar um estacionamento",
            description = "Desativa um estacionamento existente com base no ID fornecido.",
            tags = {"Estacionamento controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Estacionamento desativado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Estacionamento não encontrado"
                    )
            }
    )
    public ResponseEntity<Void> desativarEstacionamento(@PathVariable Long id) {
        service.desativarEstacionamento(id);
        return ResponseEntity.noContent().build();
    }
}
