package com.reserva.exception;

public class RecursoNaoEncontradoException extends ReservaException {
    public RecursoNaoEncontradoException(int id) {
        super("Recurso não encontrado com id: " + id);
    }
}
