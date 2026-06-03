package com.reserva.model;

public enum TipoRecurso {
    LABORATORIO,
    SALA,
    PROJETOR,
    NOTEBOOK,
    KIT_AULA,
    EQUIPAMENTO_FRAGIL;

    @Override
    public String toString() {
        return switch (this) {
            case LABORATORIO       -> "Laboratório";
            case SALA              -> "Sala";
            case PROJETOR          -> "Projetor";
            case NOTEBOOK          -> "Notebook";
            case KIT_AULA          -> "Kit de Aula";
            case EQUIPAMENTO_FRAGIL -> "Equipamento Frágil";
        };
    }
}
