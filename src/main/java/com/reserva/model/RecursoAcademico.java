package com.reserva.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata base para todos os recursos acadêmicos.
 * Demonstra: Herança, Encapsulamento, Interface, Polimorfismo.
 */
public abstract class RecursoAcademico implements Reservavel {

    private static int contadorId = 1;

    private final int id;
    private String codigo;
    private String descricao;
    private TipoRecurso tipo;
    private int capacidade;
    private boolean disponivel;
    private String localizacao;
    private String restricoesUso;
    private boolean requerAcompanhamentoTecnico;
    private final List<Reserva> reservas = new ArrayList<>();

    protected RecursoAcademico(String codigo, String descricao, TipoRecurso tipo,
                                int capacidade, String localizacao) {
        this.id = contadorId++;
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipo = tipo;
        this.capacidade = capacidade;
        this.localizacao = localizacao;
        this.disponivel = true;
    }

    /**
     * Verifica se o recurso está livre no período solicitado.
     * Implementação da interface Reservavel.
     */
    @Override
    public boolean verificarDisponibilidade(LocalDateTime inicio, LocalDateTime fim) {
        if (!disponivel) return false;
        return reservas.stream()
                .filter(r -> r.getStatus() == StatusReserva.APROVADA
                          || r.getStatus() == StatusReserva.PENDENTE)
                .noneMatch(r -> r.conflitaCom(inicio, fim));
    }

    /**
     * Método abstrato — cada subclasse define suas regras de penalidade.
     * Demonstra: Polimorfismo.
     */
    public abstract String calcularPenalidade(boolean atraso, boolean avaria);

    public void adicionarReserva(Reserva r) { reservas.add(r); }

    public void removerReserva(Reserva r) { reservas.remove(r); }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public int getId() { return id; }

    @Override public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    @Override public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public TipoRecurso getTipo() { return tipo; }
    public void setTipo(TipoRecurso tipo) { this.tipo = tipo; }

    @Override public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    @Override public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }

    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }

    public String getRestricoesUso() { return restricoesUso; }
    public void setRestricoesUso(String restricoesUso) { this.restricoesUso = restricoesUso; }

    public boolean isRequerAcompanhamentoTecnico() { return requerAcompanhamentoTecnico; }
    public void setRequerAcompanhamentoTecnico(boolean req) { this.requerAcompanhamentoTecnico = req; }

    public List<Reserva> getReservas() { return reservas; }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | Cap: %d | Local: %s | %s",
                id, codigo, tipo, capacidade, localizacao,
                disponivel ? "Disponível" : "Indisponível");
    }

    public static void resetContador() { contadorId = 1; }
}
