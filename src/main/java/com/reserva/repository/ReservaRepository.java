package com.reserva.repository;

import com.reserva.model.Reserva;
import com.reserva.model.StatusReserva;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ReservaRepository {

    private final List<Reserva> reservas = new ArrayList<>();

    public void salvar(Reserva r) {
        reservas.removeIf(x -> x.getId() == r.getId());
        reservas.add(r);
    }

    public Optional<Reserva> buscarPorId(int id) {
        return reservas.stream().filter(r -> r.getId() == id).findFirst();
    }

    public List<Reserva> listarTodas() {
        return Collections.unmodifiableList(reservas);
    }

    public List<Reserva> listarPorUsuario(int usuarioId) {
        return reservas.stream()
                .filter(r -> r.getResponsavel().getId() == usuarioId)
                .collect(Collectors.toList());
    }

    public List<Reserva> listarPorRecurso(int recursoId) {
        return reservas.stream()
                .filter(r -> r.getRecurso().getId() == recursoId)
                .collect(Collectors.toList());
    }

    public List<Reserva> listarPorStatus(StatusReserva status) {
        return reservas.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Reserva> listarAtivasPorUsuario(int usuarioId) {
        return reservas.stream()
                .filter(r -> r.getResponsavel().getId() == usuarioId
                          && (r.getStatus() == StatusReserva.APROVADA
                           || r.getStatus() == StatusReserva.PENDENTE))
                .collect(Collectors.toList());
    }

    public List<Reserva> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return reservas.stream()
                .filter(r -> !r.getInicio().isAfter(fim) && !r.getFim().isBefore(inicio))
                .collect(Collectors.toList());
    }

    public void remover(int id) {
        reservas.removeIf(r -> r.getId() == id);
    }
}
