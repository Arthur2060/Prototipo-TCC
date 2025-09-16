package com.senai.TCC.application.services;

import com.senai.TCC.application.dto.create_requests.ReservaCreateRequest;
import com.senai.TCC.application.mappers.ReservaMapper;
import com.senai.TCC.application.dto.response.ReservaResponse;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.ReservaRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.Reserva;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;

    private final EstacionamentoRepository estacionamentoRepository;

    private final ClienteRepository clienteRepository;

    public ReservaService(EstacionamentoRepository estacionamentoRepository, ClienteRepository clienteRepository, ReservaRepository reservaRepository) {
        this.estacionamentoRepository = estacionamentoRepository;
        this.clienteRepository = clienteRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<ReservaResponse> listarReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaMapper::fromEntity)
                .toList();
    }

    public ReservaResponse cadastrarReserva(ReservaCreateRequest dto) {
        Reserva novaReserva = ReservaMapper.toEntity(dto);
        Optional<Cliente> optionalCliente = clienteRepository.findById(dto.clienteId());
        Optional<Estacionamento> optionalEstacionamento = estacionamentoRepository.findById(dto.estacioId());

        if (optionalCliente.isEmpty() || optionalEstacionamento.isEmpty()) {
            throw new IdNaoCadastrado("Cliente ou estacionameno não encontrado no sistema!");
        }

        Cliente cliente = optionalCliente.get();
        Estacionamento estacionamento = optionalEstacionamento.get();

        novaReserva.setCliente(cliente);
        novaReserva.setEstacionamento(estacionamento);

        estacionamento.getReservas().add(novaReserva);
        cliente.getReservas().add(novaReserva);

        novaReserva.setStatus(true);
        return ReservaMapper.fromEntity(reservaRepository.save(novaReserva));
    }

    public ReservaResponse atualizarReserva(ReservaCreateRequest dto, Long id) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new IdNaoCadastrado("ID de reserva buscado não encontrado no sistema!");
        }

        Reserva reserva = optionalReserva.get();

        reserva.setDataDaReserva(dto.dataDaReserva());
        reserva.setHoraDaReserva(dto.horaDaReserva());
        reserva.setStatus(dto.status());

        return ReservaMapper.fromEntity(reservaRepository.save(reserva));
    }

    public void deletarReserva(Long id) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new IdNaoCadastrado("ID de reserva buscado não encontrado no sistema!");
        }

        Reserva reserva = optionalReserva.get();

        reserva.getCliente().getReservas().remove(reserva);
        reserva.getEstacionamento().getReservas().remove(reserva);

        reserva.setStatus(false);
        reservaRepository.save(reserva);
    }
}
