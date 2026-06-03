package com.reserva.model;

public enum TipoUsuario {
    PROFESSOR,
    TECNICO,
    COORDENADOR,
    ALUNO_AUTORIZADO;

    @Override
    public String toString() {
        return switch (this) {
            case PROFESSOR         -> "Professor";
            case TECNICO           -> "Técnico";
            case COORDENADOR       -> "Coordenador";
            case ALUNO_AUTORIZADO  -> "Aluno Autorizado";
        };
    }
}
