package com.reserva.model;

public enum StatusReserva {
    PENDENTE,
    APROVADA,
    CANCELADA,
    CONCLUIDA,
    DEVOLVIDA_COM_ATRASO,
    DEVOLVIDA_COM_AVARIA;

    @Override
    public String toString() {
        return switch (this) {
            case PENDENTE              -> "Pendente";
            case APROVADA              -> "Aprovada";
            case CANCELADA             -> "Cancelada";
            case CONCLUIDA             -> "Concluída";
            case DEVOLVIDA_COM_ATRASO  -> "Devolvida com Atraso";
            case DEVOLVIDA_COM_AVARIA  -> "Devolvida com Avaria";
        };
    }
}
