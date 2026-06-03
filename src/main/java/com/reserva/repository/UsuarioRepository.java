package com.reserva.repository;

import com.reserva.model.Usuario;
import com.reserva.model.TipoUsuario;

import java.util.*;
import java.util.stream.Collectors;

public class UsuarioRepository {

    private final List<Usuario> usuarios = new ArrayList<>();

    public void salvar(Usuario u) {
        usuarios.removeIf(x -> x.getId() == u.getId());
        usuarios.add(u);
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarios.stream().filter(u -> u.getId() == id).findFirst();
    }

    public Optional<Usuario> buscarPorMatricula(String matricula) {
        return usuarios.stream().filter(u -> u.getMatricula().equals(matricula)).findFirst();
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarios.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst();
    }

    public List<Usuario> listarTodos() {
        return Collections.unmodifiableList(usuarios);
    }

    public List<Usuario> listarPorTipo(TipoUsuario tipo) {
        return usuarios.stream().filter(u -> u.getTipo() == tipo).collect(Collectors.toList());
    }

    public void remover(int id) {
        usuarios.removeIf(u -> u.getId() == id);
    }
}
