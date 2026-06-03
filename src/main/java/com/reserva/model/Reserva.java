package com.reserva.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reserva {

    private static int contadorId = 1;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int id;
    private final RecursoAcademico recurso;
    private final Usuario responsavel;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private StatusReserva status;
    private String finalidade;
    private boolean prioritaria;
    private final LocalDateTime criadaEm;
    private LocalDateTime devolvidaEm;
    private String observacoesDevolucao;

    public Reserva(RecursoAcademico recurso, Usuario responsavel,
                   LocalDateTime inicio, LocalDateTime fim,
                   String finalidade, boolean prioritaria) {
        this.id = contadorId++;
        this.recurso = recurso;
        this.responsavel = responsavel;
        this.inicio = inicio;
        this.fim = fim;
        this.finalidade = finalidade;
        this.prioritaria = prioritaria;
        this.status = StatusReserva.PENDENTE;
        this.criadaEm = LocalDateTime.now();
    }

    /** Verifica sobreposição de horários */
    public boolean conflitaCom(LocalDateTime novoInicio, LocalDateTime novoFim) {
        return inicio.isBefore(novoFim) && fim.isAfter(novoInicio);
    }

    public boolean estaAtrasada() {
        return devolvidaEm != null && devolvidaEm.isAfter(fim);
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public int getId() { return id; }
    public RecursoAcademico getRecurso() { return recurso; }
    public Usuario getResponsavel() { return responsavel; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }
    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }
    public String getFinalidade() { return finalidade; }
    public void setFinalidade(String finalidade) { this.finalidade = finalidade; }
    public boolean isPrioritaria() { return prioritaria; }
    public void setPrioritaria(boolean prioritaria) { this.prioritaria = prioritaria; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getDevolvidaEm() { return devolvidaEm; }
    public void setDevolvidaEm(LocalDateTime devolvidaEm) { this.devolvidaEm = devolvidaEm; }
    public String getObservacoesDevolucao() { return observacoesDevolucao; }
    public void setObservacoesDevolucao(String obs) { this.observacoesDevolucao = obs; }

    @Override
    public String toString() {
        return String.format(
                "[%d] %s | Recurso: %s | Responsável: %s | %s → %s | Status: %s%s",
                id, finalidade, recurso.getCodigo(), responsavel.getNome(),
                inicio.format(FMT), fim.format(FMT), status,
                prioritaria ? " | PRIORITÁRIA" : "");
    }

    public static void resetContador() { contadorId = 1; }
}
