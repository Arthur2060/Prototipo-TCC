package infraestructure.controller;

import application.dtos.EstacionamentoDTO;
import application.services.EstacionamentoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estacionamento")
public class EstacionamentoController {
    @Autowired
    private EstacionamentoService service;

    @GetMapping
    @Operation(
            summary = "Listar todos os estacionamentos",
            description = "Retorna uma lista de todos os estacionamentos cadastrados no sistema."
    )
    public ResponseEntity<List<EstacionamentoDTO>> listarEstacionamentos() {
        return ResponseEntity.ok(service.listarTodosOsEstacionamentos());
    }

    @PostMapping("/{id}")
    @Operation(
            summary = "Cadastrar um novo estacionamento",
            description = "Cadastra um novo estacionamento no sistema com base nos dados fornecidos.",
            tags = {"Estacionamento"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Estacionamento cadastrado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida"
                    )
            }
    )
    public ResponseEntity<EstacionamentoDTO> cadastrarEstacionamento(@RequestBody EstacionamentoDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.cadastrarEstacionamento(dto, id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar um estacionamento existente",
            description = "Atualiza os dados de um estacionamento existente com base no ID fornecido.",
            tags = {"Estacionamento"},
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
    public ResponseEntity<EstacionamentoDTO> atualizarEstacionamento(@RequestBody EstacionamentoDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarEstacionamento(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Desativar um estacionamento",
            description = "Desativa um estacionamento existente com base no ID fornecido.",
            tags = {"Estacionamento"},
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
    public void desativarEstacionamento(@PathVariable Long id) {
        service.desativarEstacionamento(id);
    }
}
