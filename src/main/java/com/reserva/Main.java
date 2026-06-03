package com.reserva;

import com.reserva.menu.*;
import com.reserva.repository.*;
import com.reserva.service.*;
import com.reserva.util.DadosIniciais;
import com.reserva.util.MenuHelper;

import java.util.Scanner;

/**
 * Classe principal — menu interativo no terminal.
 * Requisito mínimo: classe principal com menu interativo.
 */
public class Main {

    public static void main(String[] args) {

        // ── Instanciação manual das dependências ──────────────────────────
        UsuarioRepository usuarioRepo = new UsuarioRepository();
        RecursoRepository recursoRepo = new RecursoRepository();
        ReservaRepository reservaRepo = new ReservaRepository();

        UsuarioService  usuarioSvc  = new UsuarioService(usuarioRepo);
        RecursoService  recursoSvc  = new RecursoService(recursoRepo);
        ReservaService  reservaSvc  = new ReservaService(reservaRepo, usuarioRepo, recursoRepo);
        RelatorioService relSvc     = new RelatorioService(reservaRepo);

        Scanner sc = new Scanner(System.in);
        MenuHelper h = new MenuHelper(sc);

        // ── Banner ────────────────────────────────────────────────────────
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║  PLATAFORMA DE RESERVA DE AMBIENTES E   ║");
        System.out.println("║       RECURSOS ACADÊMICOS  v1.0         ║");
        System.out.println("╚══════════════════════════════════════════╝");

        // ── Dados iniciais ────────────────────────────────────────────────
        if (h.lerSimNao("Deseja carregar dados de exemplo?")) {
            DadosIniciais.popular(usuarioSvc, recursoSvc, reservaSvc);
        }

        // ── Submenus ──────────────────────────────────────────────────────
        MenuUsuario   menuUsuario   = new MenuUsuario(usuarioSvc, sc);
        MenuRecurso   menuRecurso   = new MenuRecurso(recursoSvc, sc);
        MenuReserva   menuReserva   = new MenuReserva(reservaSvc, sc);
        MenuRelatorio menuRelatorio = new MenuRelatorio(relSvc, sc);

        // ── Loop principal ────────────────────────────────────────────────
        boolean sair = false;
        while (!sair) {
            MenuHelper.titulo("MENU PRINCIPAL");
            System.out.println("  1. Usuários");
            System.out.println("  2. Recursos (salas e equipamentos)");
            System.out.println("  3. Reservas");
            System.out.println("  4. Relatórios e indicadores");
            System.out.println("  0. Sair");
            MenuHelper.separador();

            switch (h.lerInt("Opção: ")) {
                case 1 -> menuUsuario.exibir();
                case 2 -> menuRecurso.exibir();
                case 3 -> menuReserva.exibir();
                case 4 -> menuRelatorio.exibir();
                case 0 -> sair = true;
                default -> System.out.println("  ✗ Opção inválida.");
            }
        }

        System.out.println("\n  Encerrando sistema. Até logo!");
        sc.close();
    }
}
