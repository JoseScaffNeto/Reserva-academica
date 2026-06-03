package com.reserva.exception;

public class UsuarioBloqueadoException extends ReservaException {
    public UsuarioBloqueadoException(String nome) {
        super("Usuário '" + nome + "' está bloqueado e não pode realizar reservas.");
    }
}
