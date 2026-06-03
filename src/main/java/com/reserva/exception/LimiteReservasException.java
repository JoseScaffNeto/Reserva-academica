package com.reserva.exception;

public class LimiteReservasException extends ReservaException {
    public LimiteReservasException() {
        super("Limite de 10 reservas ativas simultâneas atingido.");
    }
}
