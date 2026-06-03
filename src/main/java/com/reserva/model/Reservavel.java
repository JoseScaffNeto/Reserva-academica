package com.reserva.model;

import java.time.LocalDateTime;

/**
 * Interface que define o contrato para qualquer entidade reservável.
 * Requisito: uso de Interface.
 */
public interface Reservavel {
    String getCodigo();
    String getDescricao();
    boolean isDisponivel();
    int getCapacidade();
    boolean verificarDisponibilidade(LocalDateTime inicio, LocalDateTime fim);
}
