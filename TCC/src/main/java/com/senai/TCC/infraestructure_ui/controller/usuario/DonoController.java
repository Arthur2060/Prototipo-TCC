package com.senai.TCC.infraestructure_ui.controller.usuario;

import com.senai.TCC.application.dto.requests.usuario.DonoRequest;
import com.senai.TCC.application.dto.response.usuario.ClienteResponse;
import com.senai.TCC.application.dto.response.usuario.DonoResponse;
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
            description = "Gera uma lista de todos os dono cadastrados no sistema."
    )
    public ResponseEntity<List<DonoResponse>> listarDonos() {
        return ResponseEntity.status(200).body(service.listarDonos());
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar por ID",
            description = "Busca uma entidade em especifico através de ID"
    )
    public ResponseEntity<DonoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.status(200).body(service.buscarPorId(id));
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Cadastrar dono",
            description = "Cadastra um novo dono no sistema, inicialmente" +
                        " apenas com informações genéricas de usuario."
    )
    public ResponseEntity<DonoResponse> cadastrarDono(@RequestBody DonoRequest dto) {
        return ResponseEntity.status(201).body(service.cadastrarDono(dto));
    }

    @PutMapping("/{id}")
    @Operation(
            method = "PUT",
            summary = "Atualizar dono",
            description = "Atualiza as informações de um dono já cadastrado no sistema."
    )
    public ResponseEntity<DonoResponse> atualizarDono(@RequestBody DonoRequest dto, @PathVariable Long id) {
        return ResponseEntity.status(200).body(service.atualizarDono(dto, id));
    }

    @DeleteMapping("/{id}")
    @Operation(
            method = "DELETE",
            summary = "Deletar dono",
            description = "Apaga um dono já cadastrado do sistema."
    )
    public ResponseEntity<Void> deletarDono(@PathVariable Long id) {
        service.deletarDono(id);
        return ResponseEntity.status(204).build();
    }
}
