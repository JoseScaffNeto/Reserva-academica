package com.reserva.model;

import java.time.LocalDate;

public class Usuario {

    private static int contadorId = 1;

    private final int id;
    private String nome;
    private String email;
    private String matricula;
    private TipoUsuario tipo;
    private boolean bloqueado;
    private LocalDate dataDesbloqueio;

    public Usuario(String nome, String email, String matricula, TipoUsuario tipo) {
        this.id = contadorId++;
        this.nome = nome;
        this.email = email;
        this.matricula = matricula;
        this.tipo = tipo;
        this.bloqueado = false;
    }

    public boolean podeAprovarReservas() {
        return tipo == TipoUsuario.COORDENADOR || tipo == TipoUsuario.TECNICO;
    }

    public boolean estaAtivo() {
        if (!bloqueado) return true;
        if (dataDesbloqueio != null && !LocalDate.now().isBefore(dataDesbloqueio)) {
            bloqueado = false;
            dataDesbloqueio = null;
            return true;
        }
        return false;
    }

    public void bloquear(int dias) {
        this.bloqueado = true;
        this.dataDesbloqueio = LocalDate.now().plusDays(dias);
    }

    // ── Getters / Setters ──────────────────────────────────────────────────
    public int getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }
    public boolean isBloqueado() { return bloqueado; }
    public void setBloqueado(boolean bloqueado) { this.bloqueado = bloqueado; }
    public LocalDate getDataDesbloqueio() { return dataDesbloqueio; }

    @Override
    public String toString() {
        String status = bloqueado
                ? "BLOQUEADO até " + dataDesbloqueio
                : "Ativo";
        return String.format("[%d] %s | %s | Mat: %s | %s",
                id, nome, tipo, matricula, status);
    }

    public static void resetContador() { contadorId = 1; }
}
