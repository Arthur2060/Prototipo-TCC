package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.requests.AcessoRequest;
import com.senai.TCC.application.mappers.AcessoMapper;
import com.senai.TCC.application.dto.response.AcessoResponse;
import com.senai.TCC.infraestructure.repositories.AcessoRepository;
import com.senai.TCC.infraestructure.repositories.CarroRepository;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.model.entities.Acesso;
import com.senai.TCC.model.entities.Carro;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.exceptions.IdNaoCadastradoException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AcessoService {
    private final AcessoRepository acessoRepository;

    private final EstacionamentoRepository estacionamentoRepository;

    private final CarroRepository carroRepository;

    public AcessoService(AcessoRepository acessoRepository,
                         EstacionamentoRepository estacionamentoRepository,
                         CarroRepository carroRepository) {
        this.acessoRepository = acessoRepository;
        this.estacionamentoRepository = estacionamentoRepository;
        this.carroRepository = carroRepository;
    }

    public List<AcessoResponse> listarAcessos() {
        return acessoRepository.findByStatusTrue()
                .stream()
                .map(AcessoMapper::fromEntity)
                .toList();
    }

    public AcessoResponse buscarPorId(Long id) {
        Optional<Acesso> optionalAcesso = acessoRepository.findById(id);

        if (optionalAcesso.isEmpty()) {
            throw new IdNaoCadastradoException("ID buscado não foi encontrado no sistema!");
        }

        return AcessoMapper.fromEntity(optionalAcesso.get());
    }

    @Transactional
    public AcessoResponse cadastrarAcesso(AcessoRequest dto) {
        Acesso acesso = AcessoMapper.toEntity(dto);

        acesso.calcularHorasTotais();

        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacioId());
        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastradoException("Id do estacionamento não encontrado no sistema");
        }

        Optional<Carro> optCarro = carroRepository.findByPlaca(dto.placaDoCarro());

        optCarro.ifPresent(acesso::setCarro);

        Estacionamento estacionamento = optEstacionamento.get();

        acesso.setEstacionamento(estacionamento);
        estacionamento.getAcessos().add(acesso);

        acesso.setStatus(true);
        return AcessoMapper.fromEntity(acessoRepository.save(acesso));
    }

    @Transactional
    public AcessoResponse atualizarAcesso(AcessoRequest dto, Long id) {
        Optional<Acesso> optAcesso = acessoRepository.findById(id);
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacioId());

        if (optAcesso.isEmpty() || optEstacionamento.isEmpty()) {
            throw new IdNaoCadastradoException("O Acesso ou estacionamento buscado não existe no sistema");
        }

        Acesso acesso = optAcesso.get();
        Estacionamento estacionamento = optEstacionamento.get();

        acesso.setHoraDeEntrada(dto.horaDeEntrada());
        acesso.setHoraDeSaida(dto.horaDeSaida());
        acesso.calcularHorasTotais();
        acesso.setPlacaDoCarro(dto.placaDoCarro());
        acesso.setValorAPagar(dto.valorAPagar());
        acesso.setEstacionamento(estacionamento);

        if (!estacionamento.getId()
                .equals(
                        acesso.getEstacionamento().getId()
                )
        ) {
            acesso.getEstacionamento().getAcessos().remove(acesso);
            estacionamento.getAcessos().add(acesso);
            acesso.setEstacionamento(estacionamento);
        }

        return AcessoMapper.fromEntity(acessoRepository.save(optAcesso.get()));
    }

    @Transactional
    public void deletarAcesso(Long id) {
        Optional<Acesso> optAcesso = acessoRepository.findById(id);

        if (optAcesso.isEmpty()) {
            throw new IdNaoCadastradoException("Acesso buscado não cadastrado no sistema");
        }

        Acesso acesso = optAcesso.get();
        Estacionamento estacionamento = acesso.getEstacionamento();

        estacionamento.getAcessos().remove(acesso);

        acesso.setStatus(false);
        acessoRepository.save(acesso);
    }
}
