package com.reserva.menu;

import com.reserva.model.TipoUsuario;
import com.reserva.model.Usuario;
import com.reserva.service.UsuarioService;
import com.reserva.util.MenuHelper;

import java.util.List;
import java.util.Scanner;

public class MenuUsuario {

    private final UsuarioService svc;
    private final MenuHelper h;

    public MenuUsuario(UsuarioService svc, Scanner sc) {
        this.svc = svc;
        this.h   = new MenuHelper(sc);
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            MenuHelper.titulo("MENU — USUÁRIOS");
            System.out.println("  1. Cadastrar usuário");
            System.out.println("  2. Listar todos os usuários");
            System.out.println("  3. Listar por tipo");
            System.out.println("  4. Buscar por ID");
            System.out.println("  0. Voltar");
            MenuHelper.separador();

            switch (h.lerInt("Opção: ")) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> listarPorTipo();
                case 4 -> buscar();
                case 0 -> voltar = true;
                default -> System.out.println("  ✗ Opção inválida.");
            }
        }
    }

    private void cadastrar() {
        MenuHelper.titulo("CADASTRAR USUÁRIO");
        String nome      = h.lerString("Nome       : ");
        String email     = h.lerString("E-mail     : ");
        String matricula = h.lerString("Matrícula  : ");
        TipoUsuario tipo = lerTipo();
        try {
            Usuario u = svc.cadastrar(nome, email, matricula, tipo);
            System.out.println("  ✔ Usuário cadastrado: " + u);
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }

    private void listar() {
        MenuHelper.titulo("LISTA DE USUÁRIOS");
        List<Usuario> lista = svc.listarTodos();
        if (lista.isEmpty()) System.out.println("  Nenhum usuário cadastrado.");
        else lista.forEach(u -> System.out.println("  " + u));
        h.pausar();
    }

    private void listarPorTipo() {
        TipoUsuario tipo = lerTipo();
        MenuHelper.titulo("USUÁRIOS — " + tipo);
        List<Usuario> lista = svc.listarPorTipo(tipo);
        if (lista.isEmpty()) System.out.println("  Nenhum usuário encontrado.");
        else lista.forEach(u -> System.out.println("  " + u));
        h.pausar();
    }

    private void buscar() {
        int id = h.lerInt("ID do usuário: ");
        try {
            System.out.println("  " + svc.buscarPorId(id));
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }

    private TipoUsuario lerTipo() {
        System.out.println("  Tipos: 1-PROFESSOR  2-TÉCNICO  3-COORDENADOR  4-ALUNO");
        int op = h.lerInt("  Tipo: ");
        return switch (op) {
            case 1 -> TipoUsuario.PROFESSOR;
            case 2 -> TipoUsuario.TECNICO;
            case 3 -> TipoUsuario.COORDENADOR;
            default -> TipoUsuario.ALUNO_AUTORIZADO;
        };
    }
}
