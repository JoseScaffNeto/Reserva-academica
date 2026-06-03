package com.reserva.service;

import com.reserva.exception.*;
import com.reserva.model.*;
import com.reserva.repository.*;

import java.time.LocalDateTime;
import java.util.List;

public class ReservaService {

    private static final int LIMITE_ATIVAS = 10;

    private final ReservaRepository reservaRepo;
    private final UsuarioRepository usuarioRepo;
    private final RecursoRepository recursoRepo;

    public ReservaService(ReservaRepository reservaRepo,
                          UsuarioRepository usuarioRepo,
                          RecursoRepository recursoRepo) {
        this.reservaRepo = reservaRepo;
        this.usuarioRepo = usuarioRepo;
        this.recursoRepo = recursoRepo;
    }

    /**
     * Cria uma reserva aplicando todas as regras:
     *  - usuário ativo e não bloqueado
     *  - limite de 10 reservas ativas por usuário
     *  - sem conflito de horário no recurso
     *  - aprovação automática ou pendente conforme perfil
     */
    public Reserva criarReserva(int usuarioId, int recursoId,
                                LocalDateTime inicio, LocalDateTime fim,
                                String finalidade, boolean prioritaria) {

        Usuario usuario = usuarioRepo.buscarPorId(usuarioId)
                .orElseThrow(() -> new ReservaException("Usuário não encontrado: " + usuarioId));

        if (!usuario.estaAtivo()) {
            throw new UsuarioBloqueadoException(usuario.getNome());
        }

        if (reservaRepo.listarAtivasPorUsuario(usuarioId).size() >= LIMITE_ATIVAS) {
            throw new LimiteReservasException();
        }

        RecursoAcademico recurso = recursoRepo.buscarPorId(recursoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(recursoId));

        if (!recurso.verificarDisponibilidade(inicio, fim)) {
            throw new ConflitoPeriodoException(recurso.getCodigo());
        }

        Reserva reserva = new Reserva(recurso, usuario, inicio, fim, finalidade, prioritaria);

        // Coordenador aprova automaticamente qualquer reserva
        // Reserva não-prioritária: aprovada automaticamente
        // Reserva prioritária de outro perfil: fica pendente para validação
        if (usuario.getTipo() == TipoUsuario.COORDENADOR || !prioritaria) {
            reserva.setStatus(StatusReserva.APROVADA);
        } else {
            reserva.setStatus(StatusReserva.PENDENTE);
        }

        recurso.adicionarReserva(reserva);
        reservaRepo.salvar(reserva);
        return reserva;
    }

    /** Aprovação manual por coordenador ou técnico */
    public Reserva aprovar(int reservaId, int aprovadorId) {
        Reserva reserva = buscarReserva(reservaId);
        Usuario aprovador = usuarioRepo.buscarPorId(aprovadorId)
                .orElseThrow(() -> new ReservaException("Aprovador não encontrado: " + aprovadorId));

        if (!aprovador.podeAprovarReservas()) {
            throw new ReservaException("Somente coordenadores e técnicos podem aprovar reservas.");
        }
        reserva.setStatus(StatusReserva.APROVADA);
        reservaRepo.salvar(reserva);
        return reserva;
    }

    /** Cancelamento de reserva */
    public Reserva cancelar(int reservaId) {
        Reserva reserva = buscarReserva(reservaId);
        if (reserva.getStatus() == StatusReserva.CONCLUIDA) {
            throw new ReservaException("Reserva já concluída não pode ser cancelada.");
        }
        reserva.setStatus(StatusReserva.CANCELADA);
        reserva.getRecurso().removerReserva(reserva);
        reservaRepo.salvar(reserva);
        return reserva;
    }

    /**
     * Registra devolução, calcula penalidade e aplica bloqueio ao usuário se necessário.
     */
    public String registrarDevolucao(int reservaId, boolean avaria, String observacoes) {
        Reserva reserva = buscarReserva(reservaId);

        if (reserva.getStatus() != StatusReserva.APROVADA) {
            throw new ReservaException("Só é possível devolver reservas com status APROVADA.");
        }

        LocalDateTime agora = LocalDateTime.now();
        reserva.setDevolvidaEm(agora);
        reserva.setObservacoesDevolucao(observacoes);

        boolean atraso = agora.isAfter(reserva.getFim());
        String penalidade = reserva.getRecurso().calcularPenalidade(atraso, avaria);

        if (avaria) {
            reserva.setStatus(StatusReserva.DEVOLVIDA_COM_AVARIA);
        } else if (atraso) {
            reserva.setStatus(StatusReserva.DEVOLVIDA_COM_ATRASO);
        } else {
            reserva.setStatus(StatusReserva.CONCLUIDA);
        }

        // Aplica bloqueio conforme penalidade
        if (penalidade.contains("BLOQUEIO_30_DIAS")) {
            reserva.getResponsavel().bloquear(30);
        } else if (penalidade.contains("BLOQUEIO_7_DIAS")) {
            reserva.getResponsavel().bloquear(7);
        }

        reserva.getRecurso().removerReserva(reserva);
        reservaRepo.salvar(reserva);
        usuarioRepo.salvar(reserva.getResponsavel());
        return penalidade;
    }

    // ── Consultas ──────────────────────────────────────────────────────────

    public List<Reserva> listarTodas() { return reservaRepo.listarTodas(); }

    public List<Reserva> listarPorUsuario(int usuarioId) {
        return reservaRepo.listarPorUsuario(usuarioId);
    }

    public List<Reserva> listarPorStatus(StatusReserva status) {
        return reservaRepo.listarPorStatus(status);
    }

    public List<Reserva> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return reservaRepo.listarPorPeriodo(inicio, fim);
    }

    public Reserva buscarReserva(int id) {
        return reservaRepo.buscarPorId(id)
                .orElseThrow(() -> new ReservaException("Reserva não encontrada: " + id));
    }
}
