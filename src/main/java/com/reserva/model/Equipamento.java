package com.reserva.model;

/**
 * Equipamento portátil (projetor, notebook, kit de aula).
 * Herda RecursoAcademico — regras de penalidade mais severas.
 */
public class Equipamento extends RecursoAcademico {

    private boolean fragil;
    private String numeroSerie;

    public Equipamento(String codigo, String descricao, TipoRecurso tipo,
                       String localizacao, boolean fragil) {
        super(codigo, descricao, tipo, 1, localizacao);
        this.fragil = fragil;
        if (fragil) setRequerAcompanhamentoTecnico(true);
    }

    public Equipamento(String codigo, String descricao, TipoRecurso tipo, String localizacao) {
        this(codigo, descricao, tipo, localizacao, false);
    }

    /**
     * Equipamentos frágeis geram bloqueio severo por avaria.
     * Demonstra Polimorfismo — comportamento diferente de Sala.
     */
    @Override
    public String calcularPenalidade(boolean atraso, boolean avaria) {
        StringBuilder pen = new StringBuilder();
        if (atraso) {
            pen.append("BLOQUEIO_7_DIAS: Usuário bloqueado por 7 dias por atraso. ");
        }
        if (avaria && fragil) {
            pen.append("BLOQUEIO_30_DIAS: Equipamento frágil avariado. Bloqueio por 30 dias. Registro de ocorrência.");
        } else if (avaria) {
            pen.append("REGISTRO_OCORRENCIA: Avaria registrada. Encaminhar ao técnico.");
        }
        return pen.length() > 0 ? pen.toString().trim() : "SEM_PENALIDADE";
    }

    public boolean isFragil() { return fragil; }
    public void setFragil(boolean fragil) { this.fragil = fragil; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    @Override
    public String toString() {
        return super.toString()
                + (fragil ? " | FRÁGIL" : "")
                + (numeroSerie != null ? " | S/N: " + numeroSerie : "");
    }
}
