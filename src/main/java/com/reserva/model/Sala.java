package com.reserva.model;

/**
 * Sala ou laboratório físico.
 * Herda RecursoAcademico — demonstra Herança.
 */
public class Sala extends RecursoAcademico {

    private boolean temProjetor;
    private boolean temArCondicionado;

    public Sala(String codigo, String descricao, int capacidade,
                String localizacao, boolean temProjetor, boolean temArCondicionado) {
        super(codigo, descricao, TipoRecurso.SALA, capacidade, localizacao);
        this.temProjetor = temProjetor;
        this.temArCondicionado = temArCondicionado;
    }

    public Sala(String codigo, String descricao, int capacidade, String localizacao) {
        this(codigo, descricao, capacidade, localizacao, false, false);
    }

    /**
     * Salas só geram aviso de ocorrência por atraso.
     * Demonstra Polimorfismo — comportamento diferente do Equipamento.
     */
    @Override
    public String calcularPenalidade(boolean atraso, boolean avaria) {
        if (atraso) {
            return "AVISO: Atraso na devolução da sala. Registro de ocorrência gerado.";
        }
        return "SEM_PENALIDADE";
    }

    public boolean isTemProjetor() { return temProjetor; }
    public void setTemProjetor(boolean temProjetor) { this.temProjetor = temProjetor; }

    public boolean isTemArCondicionado() { return temArCondicionado; }
    public void setTemArCondicionado(boolean v) { this.temArCondicionado = v; }

    @Override
    public String toString() {
        return super.toString()
                + (temProjetor ? " | Projetor: Sim" : "")
                + (temArCondicionado ? " | A/C: Sim" : "");
    }
}
