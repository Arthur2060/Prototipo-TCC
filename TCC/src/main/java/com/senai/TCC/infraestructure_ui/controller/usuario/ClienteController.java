package com.senai.TCC.infraestructure_ui.controller.usuario;

import com.senai.TCC.application.dto.usuarioDTO.ClienteDTO;
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
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @Operation(
            summary = "Listar todos os clientes",
            description = "Retorna uma lista de todos os clientes cadastrados no sistema.",
            tags = {"Cliente controller"}
    )
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
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
    public ResponseEntity<ClienteDTO> cadastrarCliente(@RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(clienteService.cadastrarCliente(dto));
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
    public ResponseEntity<ClienteDTO> atualizarCliente(@RequestBody ClienteDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(clienteService.atualizarCliente(dto, id));
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
    public void deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
    }
}
