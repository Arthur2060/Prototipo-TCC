package application.services;

import model.repositories.EstacionamentoRepository;
import model.repositories.usuario.DonoRepository;
import model.repositories.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstacionamentoService {
    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private DonoRepository donoRepository;
}
