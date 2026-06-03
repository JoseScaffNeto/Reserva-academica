package com.reserva.menu;

import com.reserva.service.RelatorioService;
import com.reserva.util.MenuHelper;

import java.util.Scanner;

public class MenuRelatorio {

    private final RelatorioService svc;
    private final MenuHelper h;

    public MenuRelatorio(RelatorioService svc, Scanner sc) {
        this.svc = svc;
        this.h   = new MenuHelper(sc);
    }

    public void exibir() {
        boolean voltar = false;
        while (!voltar) {
            MenuHelper.titulo("MENU — RELATÓRIOS");
            System.out.println("  1. Indicadores gerais");
            System.out.println("  2. Relatório mensal");
            System.out.println("  0. Voltar");
            MenuHelper.separador();

            switch (h.lerInt("Opção: ")) {
                case 1 -> { svc.exibirIndicadores(); h.pausar(); }
                case 2 -> mensal();
                case 0 -> voltar = true;
                default -> System.out.println("  ✗ Opção inválida.");
            }
        }
    }

    private void mensal() {
        int ano = h.lerInt("Ano : ");
        int mes = h.lerInt("Mês : ");
        try {
            svc.exibirRelatorioMensal(ano, mes);
        } catch (Exception e) {
            System.out.println("  ✗ " + e.getMessage());
        }
        h.pausar();
    }
}
