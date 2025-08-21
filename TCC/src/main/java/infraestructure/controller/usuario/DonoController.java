package infraestructure.controller.usuario;

import application.dtos.usuarioDTO.DonoDTO;
import application.services.DonoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dono")
public class DonoController {
    @Autowired
    private DonoService service;

    @GetMapping
    public ResponseEntity<List<DonoDTO>> listarDonos() {
        return ResponseEntity.ok(service.listarDonos());
    }

    @PostMapping
    public ResponseEntity<DonoDTO> cadastrarDono(@RequestBody DonoDTO dto) {
        return ResponseEntity.ok(service.cadastrarDono(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonoDTO> atualizarDono(@RequestBody DonoDTO dto, @PathVariable Long id) {
        return ResponseEntity.ok(service.atualizarDono(dto, id));
    }

    @DeleteMapping("/{id}")
    public void deletarDono(@PathVariable Long id) {
        service.deletarDono(id);
    }
}
