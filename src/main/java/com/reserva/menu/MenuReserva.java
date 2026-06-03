package com.reserva.menu;

import com.reserva.model.Reserva;
import com.reserva.model.StatusReserva;
import com.reserva.service.ReservaService;
import com.reserva.util.MenuHelper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MenuReserva {

    private final ReservaService svc;
    private final MenuHelper h;

    public MenuReserva(ReservaService svc, Scanner sc) {
        this.svc = svc;
        this.h   = new MenuHelper(sc);
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            MenuHelper.titulo("MENU — RESERVAS");
            System.out.println("  1. Nova reserva");
            System.out.println("  2. Listar todas");
            System.out.println("  3. Listar por usuário");
            System.out.println("  4. Listar por status");
            System.out.println("  5. Listar por período");
            System.out.println("  6. Aprovar reserva pendente");
            System.out.println("  7. Cancelar reserva");
            System.out.println("  8. Registrar devolução");
            System.out.println("  0. Voltar");
            MenuHelper.separador();

            switch (h.lerInt("Opção: ")) {
                case 1 -> novaReserva();
                case 2 -> listar(svc.listarTodas());
                case 3 -> listarPorUsuario();
                case 4 -> listarPorStatus();
                case 5 -> listarPorPeriodo();
                case 6 -> aprovar();
                case 7 -> cancelar();
                case 8 -> devolver();
                case 0 -> voltar = true;
                default -> System.out.println("  ✗ Opção inválida.");
            }
        }
    }

    private void novaReserva() {
        MenuHelper.titulo("NOVA RESERVA");
        int usuarioId  = h.lerInt("ID do usuário  : ");
        int recursoId  = h.lerInt("ID do recurso  : ");
        LocalDateTime inicio = h.lerDataHora("Início");
        LocalDateTime fim    = h.lerDataHora("Fim   ");
        String finalidade    = h.lerString("Finalidade     : ");
        boolean prioritaria  = h.lerSimNao("É prioritária?");
        try {
            Reserva r = svc.criarReserva(usuarioId, recursoId, inicio, fim, finalidade, prioritaria);
            System.out.println("  ✔ Reserva criada: " + r);
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }

    private void listar(List<Reserva> lista) {
        if (lista.isEmpty()) System.out.println("  Nenhuma reserva encontrada.");
        else lista.forEach(r -> System.out.println("  " + r));
        h.pausar();
    }

    private void listarPorUsuario() {
        int id = h.lerInt("ID do usuário: ");
        listar(svc.listarPorUsuario(id));
    }

    private void listarPorStatus() {
        System.out.println("  1-PENDENTE  2-APROVADA  3-CANCELADA  4-CONCLUIDA  5-ATRASO  6-AVARIA");
        StatusReserva st = switch (h.lerInt("  Status: ")) {
            case 1 -> StatusReserva.PENDENTE;
            case 2 -> StatusReserva.APROVADA;
            case 3 -> StatusReserva.CANCELADA;
            case 4 -> StatusReserva.CONCLUIDA;
            case 5 -> StatusReserva.DEVOLVIDA_COM_ATRASO;
            default -> StatusReserva.DEVOLVIDA_COM_AVARIA;
        };
        listar(svc.listarPorStatus(st));
    }

    private void listarPorPeriodo() {
        MenuHelper.titulo("RESERVAS POR PERÍODO");
        LocalDateTime inicio = h.lerDataHora("Inicio");
        LocalDateTime fim    = h.lerDataHora("Fim");
        listar(svc.listarPorPeriodo(inicio, fim));
    }

    private void aprovar() {
        MenuHelper.titulo("APROVAR RESERVA");
        int reservaId  = h.lerInt("ID da reserva  : ");
        int aprovadorId = h.lerInt("ID do aprovador: ");
        try {
            Reserva r = svc.aprovar(reservaId, aprovadorId);
            System.out.println("  ✔ Aprovada: " + r);
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }

    private void cancelar() {
        int id = h.lerInt("ID da reserva a cancelar: ");
        try {
            Reserva r = svc.cancelar(id);
            System.out.println("  ✔ Cancelada: " + r);
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }

    private void devolver() {
        MenuHelper.titulo("REGISTRAR DEVOLUÇÃO");
        int id        = h.lerInt("ID da reserva : ");
        boolean avaria = h.lerSimNao("Houve avaria?");
        String obs     = h.lerString("Observações   : ");
        try {
            String pen = svc.registrarDevolucao(id, avaria, obs);
            System.out.println("  ✔ Devolução registrada.");
            if (!pen.equals("SEM_PENALIDADE"))
                System.out.println("  ⚪  Penalidade: " + pen);
            else
                System.out.println("  ✔ Sem penalidades.");
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }
}
