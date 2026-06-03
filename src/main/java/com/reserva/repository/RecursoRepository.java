package com.reserva.repository;

import com.reserva.model.RecursoAcademico;
import com.reserva.model.TipoRecurso;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class RecursoRepository {

    private final List<RecursoAcademico> recursos = new ArrayList<>();

    public void salvar(RecursoAcademico r) {
        recursos.removeIf(x -> x.getId() == r.getId());
        recursos.add(r);
    }

    public Optional<RecursoAcademico> buscarPorId(int id) {
        return recursos.stream().filter(r -> r.getId() == id).findFirst();
    }

    public Optional<RecursoAcademico> buscarPorCodigo(String codigo) {
        return recursos.stream().filter(r -> r.getCodigo().equalsIgnoreCase(codigo)).findFirst();
    }

    public List<RecursoAcademico> listarTodos() {
        return Collections.unmodifiableList(recursos);
    }

    public List<RecursoAcademico> listarPorTipo(TipoRecurso tipo) {
        return recursos.stream().filter(r -> r.getTipo() == tipo).collect(Collectors.toList());
    }

    public List<RecursoAcademico> listarDisponiveisNoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return recursos.stream()
                .filter(r -> r.verificarDisponibilidade(inicio, fim))
                .collect(Collectors.toList());
    }

    public void remover(int id) {
        recursos.removeIf(r -> r.getId() == id);
    }
}
