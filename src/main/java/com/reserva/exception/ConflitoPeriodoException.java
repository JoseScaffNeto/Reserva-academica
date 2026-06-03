package com.reserva.exception;

public class ConflitoPeriodoException extends ReservaException {
    public ConflitoPeriodoException(String codigo) {
        super("Conflito de horário: recurso '" + codigo + "' já possui reserva ativa nesse período.");
    }
}
