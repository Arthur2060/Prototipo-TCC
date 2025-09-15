package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.create_requests.ValorCreateRequest;
import com.senai.TCC.application.dto.response.ValorResponse;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.ValorRepository;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.Valor;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ValorService {
    private final ValorRepository valorRepository;

    private final EstacionamentoRepository estacionamentoRepository;

    public ValorService(ValorRepository valorRepository, EstacionamentoRepository estacionamentoRepository) {
        this.valorRepository = valorRepository;
        this.estacionamentoRepository = estacionamentoRepository;
    }

    public List<ValorResponse> listarValor() {
        return valorRepository.findAll()
                .stream()
                .map(ValorResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ValorResponse cadastrarValor(ValorCreateRequest dto) {
        Valor novoValor = dto.toEntity();
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacioId());
        if (optEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Id de estacionamento especificado não encontrado no sistema");
        }
        Estacionamento estacionamento = optEstacionamento.get();

        novoValor.setEstacionamento(estacionamento);
        estacionamento.getValores().add(novoValor);

        return ValorResponse.fromEntity(valorRepository.save(novoValor));
    }

    @Transactional
    public ValorResponse atualizarValor(ValorCreateRequest dto, Long id) {
        Optional<Valor> optValor = valorRepository.findById(id);
        Optional<Estacionamento> optEstacionamento = estacionamentoRepository.findById(dto.estacioId());

        if (optEstacionamento.isEmpty() || optValor.isEmpty()) {
            throw new IdNaoCadastrado("ID de valor ou estacionamento buscados não encontrados");
        }

        Estacionamento estacionamento = optEstacionamento.get();
        Valor valor = optValor.get();

        valor.setEstacionamento(estacionamento);
        valor.setPeriodo(dto.periodo());
        valor.setPreco(dto.preco());
        valor.setTipoDePagamento(dto.tipoDePagamento());
        valor.setPreco(dto.preco());
        valor.setTipoDeCobranca(dto.tipoDeCobranca());

        if (!estacionamento.getValores().contains(valor)) {
            estacionamento.getValores().add(valor);
        }

        return ValorResponse.fromEntity(valorRepository.save(valor));
    }

    @Transactional
    public void deletarValor(Long id) {
        Optional<Valor> optionalValor = valorRepository.findById(id);

        if (optionalValor.isEmpty()) {
            throw new IdNaoCadastrado("Id de valor especificado não encontrado no sistema");
        }

        valorRepository.delete(optionalValor.get());
    }
}
