package application.services.usuario;

import application.dtos.usuarioDTO.GerenteDTO;
import model.repositories.EstacionamentoRepository;
import model.repositories.usuario.GerenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GerenteService {
    @Autowired
    private GerenteRepository gerenteRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    public List<GerenteDTO> listarGerentes() {
        return gerenteRepository.findAll()
                .stream()
                .map(GerenteDTO::toDTO)
                .toList();
    }

    public GerenteDTO cadastrarGerente(GerenteDTO dto) {
        var gerente = dto.fromDTO();

        gerenteRepository.save(gerente);

        return dto;
    }

    public GerenteDTO atualizarGerente(GerenteDTO dto, Long id) {
        var optGerente = gerenteRepository.findById(id);

        if (optGerente.isPresent()) {
            var gerente = optGerente.get();
            gerente.setNome(dto.nome());
            gerente.setEmail(dto.email());
            gerente.setSenha(dto.senha());
            gerente.setDataNascimento(dto.dataNascimento());
            gerenteRepository.save(gerente);
        }

        return dto;
    }

    public void deletarGerente(Long id) {
        gerenteRepository.deleteById(id);
    }
}
