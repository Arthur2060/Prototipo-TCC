package com.senai.TCC.infraestructure_ui.controller.usuario;

import com.senai.TCC.application.dtos.usuarioDTO.DonoDTO;
import com.senai.TCC.application.services.usuario.DonoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dono")
@Tag(
        name = "Dono de estacionamento controller",
        description = "Realiza operações referentes ao CRUD da entidade Dono de estacionamento," +
            " eixo central do projeto necessario para a maioria," +
        "das operações"
)
public class DonoController {
    private final DonoService service;

    public DonoController(DonoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            method = "GET",
            summary = "Listar donos",
            description = "Gera uma lista de todos os dono cadastrados no sistema.",
            tags = {"Dono de estacionamento controller"}
    )
    public ResponseEntity<List<DonoDTO>> listarDonos() {
        return ResponseEntity.ok(service.listarDonos());
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar dono",
            description = "Cadastra um novo dono no sistema, inicialmente" +
                        " apenas com informações genéricas de usuario.",
            tags = {"Dono de estacionamento controller"}
    )
    public ResponseEntity<DonoDTO> cadastrarDono(@RequestBody DonoDTO dto) {
        return ResponseEntity.ok(service.cadastrarDono(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar dono",
            description = "Atualiza as informações de um dono já cadastrado no sistema.",
            tags = {"Dono de estacionamento controller"}
    )
    public ResponseEntity<DonoDTO> atualizarDono(@RequestBody DonoDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarDono(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar dono",
            description = "Apaga um dono já cadastrado do sistema.",
            tags = {"Dono de estacionamento controller"}
    )
    public void deletarDono(@PathVariable Long id) {
        service.deletarDono(id);
    }
}
