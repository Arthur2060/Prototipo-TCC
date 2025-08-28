package com.senai.TCC.application.services;

import com.senai.TCC.application.dtos.ReservaDTO;
import com.senai.TCC.infraestructure.repositories.EstacionamentoRepository;
import com.senai.TCC.infraestructure.repositories.ReservaRepository;
import com.senai.TCC.infraestructure.repositories.usuario.ClienteRepository;
import com.senai.TCC.model.entities.Estacionamento;
import com.senai.TCC.model.entities.Reserva;
import com.senai.TCC.model.entities.usuarios.Cliente;
import com.senai.TCC.model.exceptions.IdNaoCadastrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {
    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EstacionamentoRepository estacionamentoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    public List<ReservaDTO> listarReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaDTO::toDTO)
                .toList();
    }

    public ReservaDTO cadastrarReserva(ReservaDTO dto) {
        Reserva novaReserva = dto.fromDTO();
        Optional<Cliente> optionalCliente = clienteRepository.findById(dto.usuarioId());
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


        clienteRepository.save(cliente);
        estacionamentoRepository.save(estacionamento);

        return ReservaDTO.toDTO(reservaRepository.save(novaReserva));
    }

    public ReservaDTO atualizarReserva(ReservaDTO dto, Long id) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new IdNaoCadastrado("ID de reserva buscado não encontrado no sistema!");
        }

        Reserva reserva = optionalReserva.get();

        reserva.setDataDaReserva(dto.dataDaReserva());
        reserva.setHoraDaReserva(dto.horaDaReserva());
        reserva.setStatus(dto.status());

        return ReservaDTO.toDTO(reservaRepository.save(reserva));
    }

    public void deletarReserva(Long id) {
        Optional<Reserva> optionalReserva = reservaRepository.findById(id);

        if (optionalReserva.isEmpty()) {
            throw new IdNaoCadastrado("ID de reserva buscado não encontrado no sistema!");
        }

        Reserva reserva = optionalReserva.get();

        reserva.getCliente().getReservas().remove(reserva);
        reserva.getEstacionamento().getReservas().remove(reserva);
        reservaRepository.delete(reserva);
    }
}
