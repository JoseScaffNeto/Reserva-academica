package com.reserva.service;

import com.reserva.exception.ReservaException;
import com.reserva.model.RecursoAcademico;
import com.reserva.model.TipoRecurso;
import com.reserva.repository.RecursoRepository;

import java.time.LocalDateTime;
import java.util.List;

public class RecursoService {

    private final RecursoRepository repo;

    public RecursoService(RecursoRepository repo) {
        this.repo = repo;
    }

    public RecursoAcademico cadastrar(RecursoAcademico recurso) {
        repo.buscarPorCodigo(recurso.getCodigo()).ifPresent(r -> {
            throw new ReservaException("Código já cadastrado: " + recurso.getCodigo());
        });
        repo.salvar(recurso);
        return recurso;
    }

    public RecursoAcademico buscarPorId(int id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new ReservaException("Recurso não encontrado: " + id));
    }

    public List<RecursoAcademico> listarTodos() { return repo.listarTodos(); }

    public List<RecursoAcademico> listarPorTipo(TipoRecurso tipo) { return repo.listarPorTipo(tipo); }

    public List<RecursoAcademico> listarDisponiveisNoPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repo.listarDisponiveisNoPeriodo(inicio, fim);
    }

    public void desativar(int id) {
        RecursoAcademico r = buscarPorId(id);
        r.setDisponivel(false);
        repo.salvar(r);
    }
}
