package com.senai.TCC.infraestructure_ui.controller.usuario;

import com.senai.TCC.application.dto.requests.usuario.ClienteRequest;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.application.services.usuario.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente")
@Tag(
        name = "Cliente controller",
        description = "Realiza operações relacionadas a clientes."
)
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os clientes",
            description = "Retorna uma lista de todos os clientes cadastrados no sistema.",
            tags = {"Cliente controller"}
    )
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.status(200).body(service.listarClientes());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma entidade em especifico através de ID",
            tags = {"Cliente Controller"}
    )
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            summary = "Cadastrar um novo cliente",
            description = "Cadastra um novo cliente no sistema com base nos dados fornecidos.",
            tags = {"Cliente controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Cliente cadastrado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida"
                    )
            }
    )
    public ResponseEntity<ClienteResponse> cadastrarCliente(@RequestBody ClienteRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarCliente(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar um cliente existente",
            description = "Atualiza os dados de um cliente existente com base no ID fornecido.",
            tags = {"Cliente controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Cliente atualizado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado"
                    )
            }
    )
    public ResponseEntity<ClienteResponse> atualizarCliente(@RequestBody ClienteRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarCliente(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Deletar um cliente",
            description = "Deleta um cliente existente com base no ID fornecido.",
            tags = {"Cliente controller"},
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Cliente deletado com sucesso"
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "Cliente não encontrado"
                    )
            }
    )
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        service.deletarCliente(id);
        return ResponseEntity.status(204).build();
    }
}
