package com.reserva.service;

import com.reserva.exception.ReservaException;
import com.reserva.model.TipoUsuario;
import com.reserva.model.Usuario;
import com.reserva.repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {

    private final UsuarioRepository repo;

    public UsuarioService(UsuarioRepository repo) {
        this.repo = repo;
    }

    public Usuario cadastrar(String nome, String email, String matricula, TipoUsuario tipo) {
        repo.buscarPorEmail(email).ifPresent(u -> {
            throw new ReservaException("E-mail já cadastrado: " + email);
        });
        repo.buscarPorMatricula(matricula).ifPresent(u -> {
            throw new ReservaException("Matrícula já cadastrada: " + matricula);
        });
        Usuario u = new Usuario(nome, email, matricula, tipo);
        repo.salvar(u);
        return u;
    }

    public Usuario buscarPorId(int id) {
        return repo.buscarPorId(id)
                .orElseThrow(() -> new ReservaException("Usuário não encontrado: " + id));
    }

    public List<Usuario> listarTodos() { return repo.listarTodos(); }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) { return repo.listarPorTipo(tipo); }

    public void remover(int id) {
        buscarPorId(id);
        repo.remover(id);
    }
}
