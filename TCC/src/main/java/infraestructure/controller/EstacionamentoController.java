package infraestructure.controller;

import application.dtos.EstacionamentoDTO;
import application.services.EstacionamentoService;
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
    public ResponseEntity<List<EstacionamentoDTO>> listarEstacionamentos() {
        return ResponseEntity.ok(service.listarTodosOsEstacionamentos());
    }

    @PostMapping("/{id}")
    public ResponseEntity<EstacionamentoDTO> cadastrarEstacionamento(@RequestBody EstacionamentoDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.cadastrarEstacionamento(dto, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstacionamentoDTO> atualizarEstacionamento(@RequestBody EstacionamentoDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarEstacionamento(dto, id));
    }

    @DeleteMapping("/{id}")
    public void desativarUsuario(@PathVariable Long id) {
        service.desativarEstacionamento(id);
    }
}
