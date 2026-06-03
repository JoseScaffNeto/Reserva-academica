package com.reserva.menu;

import com.reserva.model.*;
import com.reserva.service.RecursoService;
import com.reserva.util.MenuHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MenuRecurso {

    private final RecursoService svc;
    private final MenuHelper h;

    public MenuRecurso(RecursoService svc, Scanner sc) {
        this.svc = svc;
        this.h   = new MenuHelper(sc);
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            MenuHelper.titulo("MENU — RECURSOS");
            System.out.println("  1. Cadastrar sala/laboratório");
            System.out.println("  2. Cadastrar equipamento");
            System.out.println("  3. Listar todos os recursos");
            System.out.println("  4. Listar por tipo");
            System.out.println("  5. Ver disponíveis em período");
            System.out.println("  6. Desativar recurso");
            System.out.println("  0. Voltar");
            MenuHelper.separador();

            switch (h.lerInt("Opção: ")) {
                case 1 -> cadastrarSala();
                case 2 -> cadastrarEquipamento();
                case 3 -> listar();
                case 4 -> listarPorTipo();
                case 5 -> disponiveis();
                case 6 -> desativar();
                case 0 -> voltar = true;
                default -> System.out.println("  ✗ Opção inválida.");
            }
        }
    }

    private void cadastrarSala() {
        MenuHelper.titulo("CADASTRAR SALA / LABORATÓRIO");
        String codigo    = h.lerString("Código     : ");
        String descricao = h.lerString("Descrição  : ");
        int cap          = h.lerInt("Capacidade : ");
        String local     = h.lerString("Localização: ");
        boolean proj     = h.lerSimNao("Tem projetor?");
        boolean ac       = h.lerSimNao("Tem ar-condicionado?");
        try {
            RecursoAcademico r = svc.cadastrar(new Sala(codigo, descricao, cap, local, proj, ac));
            System.out.println("  ✔ " + r);
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }

    private void cadastrarEquipamento() {
        MenuHelper.titulo("CADASTRAR EQUIPAMENTO");
        String codigo    = h.lerString("Código    : ");
        String descricao = h.lerString("Descrição : ");
        System.out.println("  Tipos: 1-PROJETOR  2-NOTEBOOK  4-EQUIPAMENTO_FRÁGIL");
        TipoRecurso tipo = switch (h.lerInt("  Tipo: ")) {
            case 1 -> TipoRecurso.PROJETOR;
            case 2 -> TipoRecurso.NOTEBOOK;
            case 3 -> TipoRecurso.KIT_AULA;
            default -> TipoRecurso.EQUIPAMENTO_FRAGIL;
        };
        String local  = h.lerString("Localização: ");
        boolean fragil = h.lerSimNao("É frágil?");
        try {
            RecursoAcademico r = svc.cadastrar(new Equipamento(codigo, descricao, tipo, local, fragil));
            System.out.println("  ✔ " + r);
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }

    private void listar() {
        MenuHelper.titulo("RECURSOS CADASTRADOS");
        List<RecursoAcademico> lista = svc.listarTodos();
        if (lista.isEmpty()) System.out.println("  Nenhum recurso cadastrado.");
        else lista.forEach(r -> System.out.println("  " + r));
        h.pausar();
    }

    private void listarPorTipo() {
        System.out.println("  1-LABORATORIO  2-SALA  3-PROJETOR  4-NOTEBOOK  5-KIT_AULA  6-EQUPF_FRÁGIL");
        TipoRecurso tipo = switch (h.lerInt("  Tipo: ")) {
            case 1 -> TipoRecurso.LABORATORIO;
            case 2 -> TipoRecurso.SALA;
            case 3 -> TipoRecurso.PROJETOR;
            case 4 -> TipoRecurso.NOTEBOOK;
            case 5 -> TipoRecurso.KIT_AULA;
            default -> TipoRecurso.EQUIPAMENTO_FRAGIL;
        };
        svc.listarPorTipo(tipo).forEach(r -> System.out.println("  " + r));
        h.pausar();
    }

    private void disponiveis() {
        MenuHelper.titulo("RECURSOS DISPONÍVEIS NO PERÍODO");
        LocalDateTime inicio = h.lerDataHora("Início");
        LocalDateTime fim    = h.lerDataHora("Fim   ");
        List<RecursoAcademico> lista = svc.listarDisponiveisNoPeriodo(inicio, fim);
        if (lista.isEmpty()) System.out.println("  Nenhum recurso disponível nesse período.");
        else lista.forEach(r -> System.out.println("  " + r));
        h.pausar();
    }

    private void desativar() {
        int id = h.lerInt("ID do recurso a desativar: ");
        try {
            svc.desativar(id);
            System.out.println("  ✔ Recurso desativado.");
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }
}
